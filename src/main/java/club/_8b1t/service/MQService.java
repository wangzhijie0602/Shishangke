package club._8b1t.service;

import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;

/**
 * 消息队列服务接口
 * 提供消息发送、消费等功能
 *
 * @author root
 */
public interface MQService {

    /**
     * 发送订单延迟取消消息
     *
     * @param orderId 订单ID
     * @param delayTimeMinutes 延迟时间（分钟）
     */
    void sendOrderCancelDelayMessage(String orderId, int delayTimeMinutes);

    /**
     * 处理订单取消逻辑
     *
     * @param orderId 订单ID
     * @param message 消息对象
     * @param channel 通道
     */
    void processOrderCancel(String orderId, Message message, Channel channel);
} 