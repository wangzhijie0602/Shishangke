package club._8b1t.model.entity;

import club._8b1t.model.enums.order.OrderStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 * @TableName order
 */
@TableName(value ="order")
@Data
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long customerId;

    /**
     * 商家ID
     */
    private Long merchantId;

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
     * 收货地址ID
     */
    private Long addressId;

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

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;
}