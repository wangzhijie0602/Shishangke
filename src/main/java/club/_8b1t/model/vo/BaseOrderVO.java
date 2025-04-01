package club._8b1t.model.vo;

import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.order.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author root
 */
@Data
public abstract class BaseOrderVO {

    /**
     * 订单ID
     */
    private String id;

    /**
     * 订单类型
     */
    private OrderType orderType;

    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 店铺名称
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
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}