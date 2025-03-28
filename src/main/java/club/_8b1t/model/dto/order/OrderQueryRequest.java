package club._8b1t.model.dto.order;

import club._8b1t.model.enums.order.OrderStatus;
import lombok.Data;

/**
 * 订单查询请求
 */
@Data
public class OrderQueryRequest {

    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 订单状态
     */
    private OrderStatus status;

} 