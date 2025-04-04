package club._8b1t.task;

import club._8b1t.model.entity.Order;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 订单定时任务
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderService orderService;

    /**
     * 定时扫描超时未支付订单并自动取消
     * 每1分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void cancelTimeoutOrders() {
        log.info("开始执行超时订单自动取消定时任务");
        
        try {
            // 计算超时时间点（当前时间减去20分钟）
            LocalDateTime timeoutPoint = LocalDateTime.now().minusMinutes(20);
            Date timeoutDate = Date.from(timeoutPoint.atZone(ZoneId.systemDefault()).toInstant());
            
            // 构建更新条件：状态为待支付且创建时间早于超时时间点
            LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Order::getStatus, OrderStatus.PENDING)
                    .lt(Order::getCreatedAt, timeoutDate)
                    .set(Order::getStatus, OrderStatus.CANCELLED);
            
            // 执行批量更新
            boolean updated = orderService.update(updateWrapper);
            
            if (updated) {
                log.info("成功取消超时订单，执行时间：{}", LocalDateTime.now());
            } else {
                log.info("没有需要取消的超时订单，执行时间：{}", LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("执行超时订单自动取消定时任务异常", e);
        }
    }
} 