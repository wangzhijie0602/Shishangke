package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商家ID
     */
    private Integer merchantId;
    
    /**
     * 商家名称
     */
    private String merchantName;
    
    /**
     * 订单编号
     */
    private String orderNumber;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付状态
     */
    private String paymentStatus;
    
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
     * 配送费
     */
    private BigDecimal deliveryFee;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime expectedTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}