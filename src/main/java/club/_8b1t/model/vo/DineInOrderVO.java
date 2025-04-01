package club._8b1t.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DineInOrderVO extends BaseOrderVO{
    /**
     * 订单ID
     */
    private String orderId;

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