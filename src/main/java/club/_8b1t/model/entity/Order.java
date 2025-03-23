package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 订单实体类
 */
@Data
@TableName("`order`")
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
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
     * 订单编号
     */
    private String orderNumber;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态(PENDING:待付款, PAID:已付款, PREPARING:准备中, DELIVERING:配送中, COMPLETED:已完成, CANCELLED:已取消)
     */
    private String status;

    /**
     * 支付方式(WECHAT:微信, ALIPAY:支付宝, CASH:现金)
     */
    private String paymentMethod;

    /**
     * 支付状态(UNPAID:未支付, PAID:已支付, REFUNDED:已退款)
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

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;
}