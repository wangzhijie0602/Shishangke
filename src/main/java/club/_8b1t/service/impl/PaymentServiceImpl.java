package club._8b1t.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.config.RabbitMQConfig;
import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.service.PaymentService;
import club._8b1t.mapper.PaymentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
* @author root
* @description 针对表【payment(支付表)】的数据库操作Service实现
* @createDate 2025-03-29 15:35:05
*/
@Service
@Slf4j
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment>
    implements PaymentService {

    @Resource
    private RabbitTemplate rabbitTemplate;
    
    @Override
    public void sendPaymentTimeoutMessage(Long paymentId) {
        try {
            log.info("发送支付单[{}]延迟关闭消息，延迟时间：10分钟", paymentId);
            // 设置消息过期时间（毫秒），10分钟 = 600000毫秒
            MessagePostProcessor messagePostProcessor = message -> {
                // 设置消息的TTL（存活时间）
                message.getMessageProperties().setExpiration("600000");
                return message;
            };
            // 发送延迟消息到延迟队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_DELAY_EXCHANGE,
                    RabbitMQConfig.PAYMENT_DELAY_ROUTING_KEY,
                    paymentId.toString(),
                    messagePostProcessor
            );
        } catch (Exception e) {
            log.error("发送支付单延迟关闭消息失败", e);
        }
    }
    
    @Override
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_PROCESS_QUEUE)
    public void processPaymentTimeout(Long paymentId, Message message, Channel channel) {
        log.info("处理支付单[{}]超时自动关闭", paymentId);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            // 使用条件更新来关闭支付订单，只关闭状态为PENDING的支付单
            LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Payment::getId, paymentId)
                    .eq(Payment::getStatus, PaymentStatus.PENDING)
                    .set(Payment::getStatus, PaymentStatus.CLOSED);
            
            boolean updated = this.update(updateWrapper);
            
            if (updated) {
                log.info("支付单[{}]已自动关闭", paymentId);
            } else {
                log.info("支付单[{}]不需要关闭或不存在", paymentId);
            }
            
            // 手动确认消息已处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理支付单[{}]自动关闭异常", paymentId, e);
            try {
                // 消息处理失败，拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }
}




