package club._8b1t.model.dto.order;

import club._8b1t.model.entity.Order;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = Order.class)
public class OrderCreateRequest {
    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 商家ID
     */
    private String merchantId;

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
     * 订单备注
     */
    private String remark;

}
