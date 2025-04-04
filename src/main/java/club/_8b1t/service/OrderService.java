package club._8b1t.service;

import club._8b1t.model.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;

/**
* @author root
* @description 针对表【base_order】的数据库操作Service
* @createDate 2025-03-29 22:56:23
*/
public interface OrderService extends IService<Order> {
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
