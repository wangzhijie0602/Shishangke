package club._8b1t.model.dto.refund;

import club._8b1t.model.entity.Refund;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AutoMapper(target = Refund.class)
public class RefundCreateRequest {

    /**
     * 订单ID
     */
    @NotNull
    private String orderId;

    /**
     * 退款金额
     */
    @NotNull
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String reason;

}
