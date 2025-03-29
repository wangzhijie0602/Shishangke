package club._8b1t.model.dto.payment;

import club._8b1t.model.entity.Payment;
import club._8b1t.model.enums.payment.PaymentMethod;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = Payment.class)
public class PaymentCreateRequest {

    private String orderId;

    private PaymentMethod paymentMethod;

}
