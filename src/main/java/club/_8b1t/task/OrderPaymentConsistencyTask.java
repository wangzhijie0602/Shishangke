package club._8b1t.task;

import club._8b1t.model.entity.Order;
import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.service.OrderService;
import club._8b1t.service.PaymentService;
import club._8b1t.service.RedisLockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单与支付状态一致性检查任务
 * 用于异步补偿可能出现的订单与支付状态不一致的情况
 */
@Component
@Slf4j
public class OrderPaymentConsistencyTask {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RedisLockService redisLockService;
    
    /**
     * 每5分钟检查一次订单和支付状态一致性
     * 主要发现并修复以下问题：
     * 1. 支付成功但订单已取消的情况
     * 2. 订单已支付但没有成功支付记录的情况
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkOrderPaymentConsistency() {
        log.info("开始执行订单-支付状态一致性检查任务");
        
        // 使用分布式锁确保任务不会并发执行
        String lockKey = "task:order_payment_consistency";
        boolean locked = redisLockService.tryLock(lockKey, 60); // 60秒锁超时
        
        if (!locked) {
            log.info("获取分布式锁失败，跳过本次执行");
            return;
        }
        
        try {
            // 检查并修复支付成功但订单已取消的异常情况
            fixSuccessPaymentWithCancelledOrder();
            
            // 检查并修复订单已支付但没有成功支付记录的情况
            fixPaidOrderWithoutSuccessPayment();
            
            log.info("订单-支付状态一致性检查任务完成");
        } catch (Exception e) {
            log.error("执行订单-支付状态一致性检查任务异常", e);
        } finally {
            redisLockService.unlock(lockKey);
        }
    }
    
    /**
     * 修复支付成功但订单已取消的异常情况
     */
    @Transactional(rollbackFor = Exception.class)
    public void fixSuccessPaymentWithCancelledOrder() {
        // 查找状态为成功的支付记录
        List<Payment> successPayments = paymentService.list(
            new LambdaQueryWrapper<Payment>()
                .eq(Payment::getStatus, PaymentStatus.SUCCESS)
        );
        
        for (Payment payment : successPayments) {
            // 对每个支付记录，获取对应的订单
            Order order = orderService.getById(payment.getOrderId());
            
            // 如果订单存在且状态为已取消，需要修复
            if (order != null && order.getStatus() == OrderStatus.CANCELLED) {
                log.warn("发现状态不一致：订单[{}]已取消但支付[{}]成功，尝试修复", 
                        order.getId(), payment.getId());
                
                // 获取分布式锁，确保修复操作的原子性
                String lockKey = "fix:order:" + order.getId();
                boolean locked = redisLockService.tryLock(lockKey, 30);
                
                if (locked) {
                    try {
                        // 再次确认状态，防止并发修改
                        Order latestOrder = orderService.getById(order.getId());
                        if (latestOrder.getStatus() == OrderStatus.CANCELLED) {
                            // 修复订单状态
                            latestOrder.setStatus(OrderStatus.PAID);
                            orderService.updateById(latestOrder);
                            
                            log.info("已修复：订单[{}]状态从已取消改为已支付", order.getId());
                            
                            // 记录异常日志供后续分析
                            recordInconsistencyEvent(order.getId(), payment.getId(), 
                                "ORDER_CANCELLED_BUT_PAYMENT_SUCCESS");
                        }
                    } finally {
                        redisLockService.unlock(lockKey);
                    }
                }
            }
        }
    }
    
    /**
     * 修复订单已支付但没有成功支付记录的情况
     */
    @Transactional(rollbackFor = Exception.class)
    public void fixPaidOrderWithoutSuccessPayment() {
        // 查找状态为已支付的订单
        List<Order> paidOrders = orderService.list(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, OrderStatus.PAID)
        );
        
        for (Order order : paidOrders) {
            // 对每个订单，查找对应的支付记录
            Payment payment = paymentService.getOne(
                new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getOrderId, order.getId())
                    .eq(Payment::getStatus, PaymentStatus.SUCCESS)
            );
            
            // 如果没有成功的支付记录，查找是否有待支付的记录
            if (payment == null) {
                Payment pendingPayment = paymentService.getOne(
                    new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderId, order.getId())
                        .eq(Payment::getStatus, PaymentStatus.PENDING)
                );
                
                // 如果有待支付记录，更新为成功状态
                if (pendingPayment != null) {
                    log.warn("发现状态不一致：订单[{}]已支付但支付记录[{}]仍为待支付状态，尝试修复", 
                            order.getId(), pendingPayment.getId());
                    
                    // 获取分布式锁
                    String lockKey = "fix:payment:" + pendingPayment.getId();
                    boolean locked = redisLockService.tryLock(lockKey, 30);
                    
                    if (locked) {
                        try {
                            // 再次确认状态
                            Payment latestPayment = paymentService.getById(pendingPayment.getId());
                            if (latestPayment.getStatus() == PaymentStatus.PENDING) {
                                // 修复支付状态
                                latestPayment.setStatus(PaymentStatus.SUCCESS);
                                latestPayment.setPaymentTime(new Date());
                                paymentService.updateById(latestPayment);
                                
                                log.info("已修复：支付记录[{}]状态从待支付改为已支付", pendingPayment.getId());
                                
                                recordInconsistencyEvent(order.getId(), pendingPayment.getId(),
                                    "ORDER_PAID_BUT_PAYMENT_PENDING");
                            }
                        } finally {
                            redisLockService.unlock(lockKey);
                        }
                    }
                } else {
                    // 记录没有支付记录但订单已支付的情况
                    log.error("发现严重状态不一致：订单[{}]已支付但没有任何支付记录", order.getId());
                    recordInconsistencyEvent(order.getId(), null, "ORDER_PAID_BUT_NO_PAYMENT");
                }
            }
        }
    }
    
    /**
     * 记录状态不一致事件
     * 实际实现中可以保存到数据库或发送通知
     */
    private void recordInconsistencyEvent(Long orderId, Long paymentId, String type) {
        // 在实际项目中，可以插入到异常日志表中或发送通知给运维人员
        log.warn("记录状态不一致事件：类型=[{}], 订单ID=[{}], 支付ID=[{}], 时间=[{}]", 
                type, orderId, paymentId, new Date());
        
        // 这里可以添加更多的处理逻辑，如发送报警邮件、短信等
    }
} 