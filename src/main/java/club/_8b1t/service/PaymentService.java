package club._8b1t.service;

import club._8b1t.model.entity.Payment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;

/**
* @author root
* @description 针对表【payment(支付表)】的数据库操作Service
* @createDate 2025-03-29 15:35:05
*/
public interface PaymentService extends IService<Payment> {
    
    /**
     * 创建支付订单时，发送延迟消息用于超时自动关闭
     * 
     * @param paymentId 支付ID
     */
    void sendPaymentTimeoutMessage(Long paymentId);
    
    /**
     * 处理支付超时关闭
     * 
     * @param paymentId 支付ID
     * @param message 消息对象
     * @param channel 通道
     */
    void processPaymentTimeout(Long paymentId, Message message, Channel channel);
}
