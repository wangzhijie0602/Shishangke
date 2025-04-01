package club._8b1t.model.dto.order;

import club._8b1t.model.entity.DeliveryOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = DeliveryOrder.class)
public class OrderAddressRequest {

    /**
     * 订单ID
     */
    private String id;

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

}
