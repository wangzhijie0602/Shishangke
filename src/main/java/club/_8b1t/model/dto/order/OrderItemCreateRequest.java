package club._8b1t.model.dto.order;

import club._8b1t.model.entity.OrderItem;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = OrderItem.class)
public class OrderItemCreateRequest {

    /**
     * 菜品ID
     */
    private String menuId;

    /**
     * 数量
     */
    private Integer quantity;

}