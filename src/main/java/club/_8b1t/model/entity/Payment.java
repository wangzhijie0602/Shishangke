package club._8b1t.model.entity;

import club._8b1t.model.enums.payment.PaymentMethod;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.model.vo.PaymentVO;
import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 支付表
 * @TableName payment
 */
@TableName(value ="payment")
@Data
@AutoMapper(target = PaymentVO.class)
public class Payment {
    /**
     * 支付ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}