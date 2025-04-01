package club._8b1t.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderAndItemVO {
    private BaseOrderVO order;
    private List<OrderItemVO> orderItem;
}
