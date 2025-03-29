package club._8b1t.model.entity;

import club._8b1t.model.enums.order.RefundStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 退款表
 * @TableName refund
 */
@TableName(value ="refund")
@Data
public class Refund {
    /**
     * 退款ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付ID
     */
    private Long paymentId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long customerId;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款状态
     */
    private RefundStatus status;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}