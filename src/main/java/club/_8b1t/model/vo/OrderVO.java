package club._8b1t.model.vo;

import club._8b1t.model.enums.order.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    
    /**
     * 订单ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String customerId;
    
    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 商家名称
     */
    private String merchantName;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 订单状态
     */
    private OrderStatus status;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 配送费
     */
    private BigDecimal deliveryFee;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 预计送达时间
     */
    private Date expectedDeliveryTime;
    
    /**
     * 实际送达时间
     */
    private Date actualDeliveryTime;

    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;

} 