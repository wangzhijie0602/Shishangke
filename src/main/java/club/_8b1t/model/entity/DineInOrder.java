package club._8b1t.model.entity;

import club._8b1t.model.vo.BaseOrderVO;
import club._8b1t.model.vo.DeliveryOrderVO;
import club._8b1t.model.vo.DineInOrderVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;

/**
 * 
 * @TableName dine_in_order
 */
@TableName(value ="dine_in_order")
@Data
@AutoMapper(target = DineInOrderVO.class)
public class DineInOrder {
    /**
     * 订单ID
     */
    @TableId(type = IdType.INPUT)
    private Long orderId;

    /**
     * 桌号
     */
    private String tableNumber;

    /**
     * 就餐人数
     */
    private Integer seatCount;

    /**
     * 入座时间
     */
    private Date checkInTime;

    /**
     * 结账时间
     */
    private Date checkOutTime;
}