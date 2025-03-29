package club._8b1t.model.entity;

import club._8b1t.model.enums.order.PaymentMethod;
import club._8b1t.model.enums.order.PaymentStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 支付表
 * @TableName payment
 */
@TableName(value ="payment")
@Data
public class Payment {
    /**
     * 支付ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long customerId;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 支付方式
     */
    private PaymentMethod paymentMethod;

    /**
     * 第三方交易号
     */
    private String transactionId;

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