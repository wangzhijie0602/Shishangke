package club._8b1t.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // 订单相关MQ配置
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.key";
    
    public static final String ORDER_PROCESS_QUEUE = "order.process.queue";
    public static final String ORDER_PROCESS_EXCHANGE = "order.process.exchange";
    public static final String ORDER_PROCESS_ROUTING_KEY = "order.process.key";
    
    // 配置RabbitMQ监听器容器工厂，设置为手动确认模式
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 设置为手动确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
    
    // 订单延迟队列配置
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_PROCESS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_PROCESS_ROUTING_KEY)
                .build();
    }
    
    @Bean
    public Queue orderProcessQueue() {
        return QueueBuilder.durable(ORDER_PROCESS_QUEUE).build();
    }
    
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }
    
    @Bean
    public DirectExchange orderProcessExchange() {
        return new DirectExchange(ORDER_PROCESS_EXCHANGE);
    }
    
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with(ORDER_DELAY_ROUTING_KEY);
    }
    
    @Bean
    public Binding orderProcessBinding() {
        return BindingBuilder.bind(orderProcessQueue()).to(orderProcessExchange()).with(ORDER_PROCESS_ROUTING_KEY);
    }
}