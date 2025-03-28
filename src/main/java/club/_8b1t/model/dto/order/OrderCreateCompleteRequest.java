package club._8b1t.model.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateCompleteRequest {

    private OrderCreateRequest orderRequest;
    private List<OrderItemCreateRequest> orderItemRequests;
    
}