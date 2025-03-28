package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项VO
 */
@Data
public class OrderItemVO {
    /**
     * 订单项ID
     */
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 菜品ID
     */
    private String menuId;

    /**
     * 菜品名称
     */
    private String menuName;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 菜品图片
     */
    private String imageUrl;
}