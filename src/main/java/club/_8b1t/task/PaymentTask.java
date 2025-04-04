package club._8b1t.task;

import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 支付定时任务
 */
@Component
@Slf4j
public class PaymentTask {

    @Autowired
    private PaymentService paymentService;

    /**
     * 定时扫描超时未完成支付并自动关闭
     * 每1分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeTimeoutPayments() {
        log.info("开始执行超时支付自动关闭定时任务");
        
        try {
            // 计算超时时间点（当前时间减去10分钟）
            LocalDateTime timeoutPoint = LocalDateTime.now().minusMinutes(10);
            Date timeoutDate = Date.from(timeoutPoint.atZone(ZoneId.systemDefault()).toInstant());
            
            // 构建更新条件：状态为待支付且创建时间早于超时时间点
            LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Payment::getStatus, PaymentStatus.PENDING)
                    .lt(Payment::getCreatedAt, timeoutDate)
                    .set(Payment::getStatus, PaymentStatus.CLOSED);
            
            // 执行批量更新
            boolean updated = paymentService.update(updateWrapper);
            
            if (updated) {
                log.info("成功关闭超时支付，执行时间：{}", LocalDateTime.now());
            } else {
                log.info("没有需要关闭的超时支付，执行时间：{}", LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("执行超时支付自动关闭定时任务异常", e);
        }
    }
} 