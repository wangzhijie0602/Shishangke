package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.model.dto.payment.PaymentCreateRequest;
import club._8b1t.model.entity.Order;
import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.payment.PaymentMethod;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.model.vo.PaymentVO;
import club._8b1t.service.OrderService;
import club._8b1t.service.PaymentService;
import club._8b1t.service.RefundService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static club._8b1t.exception.ResultCode.*;

@RestController
@RequestMapping("/api/v1/pay")
public class PaymentController {

    @Resource
    private PaymentService paymentService;
    @Resource
    private RefundService refundService;
    @Resource
    private OrderService orderService;
    @Resource
    private Converter converter;

    @PostMapping("/create")
    @Operation(operationId = "payment_create_payment")
    public Result<PaymentVO> createPayment(@RequestBody PaymentCreateRequest request) {

        Order order = orderService.getById(request.getOrderId());
        ExceptionUtil.throwIfNull(order, OPERATION_FAILED);

        Long customerId = StpUtil.getLoginIdAsLong();
        ExceptionUtil.throwIfNot(order.getCustomerId().equals(customerId), FORBIDDEN, "此订单不属于该用户,无权限访问");

        Payment payment = paymentService.getOne(new LambdaQueryWrapper<>(Payment.class)
                .eq(Payment::getOrderId, order.getId())
                .eq(Payment::getPaymentMethod, request.getPaymentMethod()));
        if (payment == null) {
            payment = converter.convert(request, Payment.class);
            payment.setCustomerId(customerId);
            payment.setOrderId(order.getId());
            payment.setPaymentAmount(order.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
            boolean success = paymentService.save(payment);
            ExceptionUtil.throwIfNot(success, OPERATION_FAILED);
        }

        PaymentVO paymentVO = converter.convert(payment, PaymentVO.class);
        return ResultUtil.success(paymentVO);
    }

    @GetMapping("/{paymentId}")
    @Operation(operationId = "payment_confirm_payment")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> confirmPayment(@PathVariable String paymentId) {

        Payment payment = paymentService.getById(paymentId);
        ExceptionUtil.throwIfNull(payment, OPERATION_FAILED);

        Long customerId = StpUtil.getLoginIdAsLong();
        ExceptionUtil.throwIfNot(payment.getCustomerId().equals(customerId), FORBIDDEN, "此订单不属于该用户,无权限访问");
        ExceptionUtil.throwIf(payment.getStatus().equals(PaymentStatus.FAILED) || payment.getStatus().equals(PaymentStatus.REFUNDED), OPERATION_FAILED);

        payment.setStatus(PaymentStatus.SUCCESS);
        Order order = orderService.getById(payment.getOrderId());
        order.setStatus(OrderStatus.PAID);

        boolean paySuccess = paymentService.updateById(payment);
        boolean orderSuccess = orderService.updateById(order);
        ExceptionUtil.throwIfNot(paySuccess && orderSuccess, OPERATION_FAILED);

        return ResultUtil.success();
    }

}
