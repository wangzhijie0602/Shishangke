package club._8b1t.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class OrderAndItemVO {
    private OrderVO order;
    private List<OrderItemVO> orderItem;
}
