package club._8b1t.service.impl;

import club._8b1t.config.RabbitMQConfig;
import club._8b1t.model.entity.Order;
import club._8b1t.model.enums.order.OrderStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.service.OrderService;
import club._8b1t.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
* @author root
* @description 针对表【base_order】的数据库操作Service实现
* @createDate 2025-03-29 22:56:23
*/
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendOrderCancelDelayMessage(String orderId, int delayTimeMinutes) {
        try {
            log.info("发送订单[{}]延迟取消消息，延迟时间：{}分钟", orderId, delayTimeMinutes);
            // 设置消息过期时间（毫秒）
            MessagePostProcessor messagePostProcessor = message -> {
                // 设置消息的TTL（存活时间）
                message.getMessageProperties().setExpiration(String.valueOf(delayTimeMinutes * 60 * 1000));
                return message;
            };
            // 发送延迟消息到延迟队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                    RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                    orderId,
                    messagePostProcessor
            );
        } catch (Exception e) {
            log.error("发送订单延迟取消消息失败", e);
        }
    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.ORDER_PROCESS_QUEUE)
    public void processOrderCancel(String orderId, Message message, Channel channel) {
        log.info("处理订单[{}]自动取消", orderId);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            // 使用条件更新来取消订单，只取消状态为PENDING的订单
            LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Order::getId, orderId)
                    .eq(Order::getStatus, OrderStatus.PENDING)
                    .set(Order::getStatus, OrderStatus.CANCELLED);

            boolean updated = this.update(updateWrapper);

            if (updated) {
                log.info("订单[{}]已自动取消", orderId);
            } else {
                log.info("订单[{}]不需要取消或不存在", orderId);
            }

            // 手动确认消息已处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理订单[{}]自动取消异常", orderId, e);
            try {
                // 消息处理失败，拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }

}




