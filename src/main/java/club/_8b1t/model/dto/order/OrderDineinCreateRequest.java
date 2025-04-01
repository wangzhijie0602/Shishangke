package club._8b1t.model.dto.order;

import club._8b1t.model.entity.DineInOrder;
import club._8b1t.model.entity.Order;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;

import java.util.Date;

@Data
@AutoMappers({
        @AutoMapper(target = Order.class),
        @AutoMapper(target = DineInOrder.class)
})
public class OrderDineinCreateRequest {

    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 商家ID
     */
    private String merchantId;

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
