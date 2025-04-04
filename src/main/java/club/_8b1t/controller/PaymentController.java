package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.model.dto.payment.PaymentCreateRequest;
import club._8b1t.model.entity.Order;
import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.model.vo.PaymentVO;
import club._8b1t.service.OrderService;
import club._8b1t.service.PaymentService;
import club._8b1t.service.RedisLockService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static club._8b1t.exception.ResultCode.*;

/**
 * 支付控制器
 * 处理支付相关操作，包括创建支付记录和确认支付等功能
 */
@RestController
@RequestMapping("/api/v1/pay")
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;
    @Resource
    private OrderService orderService;
    @Resource
    private Converter converter;
    @Resource
    private RedisLockService redisLockService;

    /**
     * 创建支付记录
     *
     * @param request 支付创建请求
     * @return 支付记录信息
     */
    @PostMapping("/create")
    @Operation(operationId = "payment_create_payment")
    public Result<PaymentVO> createPayment(@RequestBody PaymentCreateRequest request) {
        // 获取订单信息
        Order order = orderService.getById(request.getOrderId());
        ExceptionUtil.throwIfNull(order, OPERATION_FAILED);
        ExceptionUtil.throwIfNot(order.getStatus() == OrderStatus.PENDING, OPERATION_FAILED);

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        // 验证用户是否为订单所有者
        ExceptionUtil.throwIfNot(order.getCustomerId().equals(customerId), FORBIDDEN, "此订单不属于该用户,无权限访问");

        // 查询是否已存在相同支付方式的支付记录
        Payment payment = paymentService.getOne(new LambdaQueryWrapper<>(Payment.class)
                .eq(Payment::getOrderId, order.getId())
                .eq(Payment::getPaymentMethod, request.getPaymentMethod()));
                
        // 如果不存在，则创建新的支付记录
        if (payment == null) {
            payment = converter.convert(request, Payment.class);
            payment.setCustomerId(customerId);
            payment.setOrderId(order.getId());
            payment.setPaymentAmount(order.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
            boolean success = paymentService.save(payment);
            ExceptionUtil.throwIfNot(success, OPERATION_FAILED);
            
            // 发送支付超时自动关闭的延迟消息
            paymentService.sendPaymentTimeoutMessage(payment.getId());
        }

        // 转换为VO对象并返回
        PaymentVO paymentVO = converter.convert(payment, PaymentVO.class);
        return ResultUtil.success(paymentVO);
    }

    /**
     * 确认支付完成
     *
     * @param paymentId 支付ID
     * @return 操作结果
     */
    @GetMapping("/{paymentId}")
    @Operation(operationId = "payment_confirm_payment")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> confirmPayment(@PathVariable String paymentId) {
        // 构建锁key
        String lockKey = "payment:confirm:" + paymentId;
        
        // 尝试获取分布式锁，30秒超时
        boolean locked = redisLockService.tryLock(lockKey, 30);
        if (!locked) {
            throw new BusinessException(OPERATION_FAILED, "系统繁忙，请稍后再试");
        }
        
        try {
            // 获取支付记录
            Payment payment = paymentService.getById(paymentId);
            ExceptionUtil.throwIfNull(payment, OPERATION_FAILED);

            // 获取当前登录用户ID
            Long customerId = StpUtil.getLoginIdAsLong();
            // 验证用户是否为支付记录所有者
            ExceptionUtil.throwIfNot(payment.getCustomerId().equals(customerId), FORBIDDEN, "此订单不属于该用户,无权限访问");
            // 验证支付状态不是失败或已退款
            ExceptionUtil.throwIf(payment.getStatus().equals(PaymentStatus.FAILED) || payment.getStatus().equals(PaymentStatus.REFUNDED), OPERATION_FAILED);

            // 获取订单并检查订单状态
            Order order = orderService.getById(payment.getOrderId());
            ExceptionUtil.throwIfNull(order, OPERATION_FAILED);
            
            // 如果订单已被取消，则检查取消时间
            if (order.getStatus() == OrderStatus.CANCELLED) {
                // 计算订单取消时间，如果在5分钟内被取消，允许恢复
                Date now = new Date();
                long cancelTimeMillis = now.getTime() - order.getUpdatedAt().getTime();
                if (cancelTimeMillis <= 5 * 60 * 1000) { // 5分钟内
                    log.info("订单[{}]已被取消，但在支付确认时重新激活", order.getId());
                } else {
                    ExceptionUtil.throwIf(true, OPERATION_FAILED, "订单已被取消超过恢复时间，无法完成支付");
                }
            }

            // 更新支付记录状态为成功
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentTime(new Date());
            
            // 更新订单状态为已支付
            order.setStatus(OrderStatus.PAID);

            // 保存支付记录和订单状态
            boolean paySuccess = paymentService.updateById(payment);
            boolean orderSuccess = orderService.updateById(order);
            ExceptionUtil.throwIfNot(paySuccess && orderSuccess, OPERATION_FAILED);

            return ResultUtil.success();
        } finally {
            // 释放锁
            redisLockService.unlock(lockKey);
        }
    }
}
