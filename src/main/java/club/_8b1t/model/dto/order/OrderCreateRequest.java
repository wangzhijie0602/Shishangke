package club._8b1t.model.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单创建请求
 */
@Data
public class OrderCreateRequest {
    
    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Integer merchantId;
    
    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    private BigDecimal totalAmount;
    
    /**
     * 配送地址
     */
    @NotBlank(message = "配送地址不能为空")
    private String address;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 配送费
     */
    private BigDecimal deliveryFee;
    
    /**
     * 预计送达时间
     */
    private String expectedTime;
} 