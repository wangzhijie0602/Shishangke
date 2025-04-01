package club._8b1t.model.entity;

import club._8b1t.model.vo.BaseOrderVO;
import club._8b1t.model.vo.DeliveryOrderVO;
import club._8b1t.model.vo.DineInOrderVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;

/**
 * 
 * @TableName delivery_order
 */
@TableName(value ="delivery_order")
@Data
@AutoMapper(target = DeliveryOrderVO.class)
public class DeliveryOrder {
    /**
     * 订单ID
     */
    @TableId(type = IdType.INPUT)
    private Long orderId;

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
     * 预计送达时间
     */
    private Date expectedDeliveryTime;

    /**
     * 实际送达时间
     */
    private Date actualDeliveryTime;
}