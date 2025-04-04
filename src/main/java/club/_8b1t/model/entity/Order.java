package club._8b1t.model.entity;

import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.order.OrderType;
import club._8b1t.model.vo.DeliveryOrderVO;
import club._8b1t.model.vo.DineInOrderVO;
import club._8b1t.model.vo.OrderVO;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@TableName(value ="`order`")
@Data
@AutoMappers({
        @AutoMapper(target = OrderVO.class),
        @AutoMapper(target = DeliveryOrderVO.class),
        @AutoMapper(target = DineInOrderVO.class)
})
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单类型
     */
    private OrderType orderType;

    /**
     * 用户ID
     */
    private Long customerId;

    /**
     * 商家ID
     */
    private Long merchantId;

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
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;
}