package club._8b1t.model.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单更新请求
 */
@Data
public class OrderUpdateRequest {
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 支付状态
     */
    private String paymentStatus;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 配送地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 预计送达时间
     */
    private String expectedTime;
} 