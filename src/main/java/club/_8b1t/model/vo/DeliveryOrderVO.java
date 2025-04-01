package club._8b1t.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 8bit
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeliveryOrderVO extends BaseOrderVO{
    /**
     * 订单ID
     */
    private String orderId;

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