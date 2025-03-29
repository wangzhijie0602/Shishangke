package club._8b1t.model.vo;

import club._8b1t.model.enums.payment.PaymentMethod;
import club._8b1t.model.enums.payment.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付信息VO
 */
@Data
public class PaymentVO {
    /**
     * 支付ID
     */
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 支付方式
     */
    private PaymentMethod paymentMethod;

    /**
     * 支付状态
     */
    private PaymentStatus status;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
} 