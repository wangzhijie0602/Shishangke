package club._8b1t.model.dto.order;

import lombok.Data;

/**
 * 订单查询请求
 */
@Data
public class OrderQueryRequest {
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 商家ID
     */
    private Integer merchantId;
    
    /**
     * 支付状态
     */
    private String paymentStatus;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 