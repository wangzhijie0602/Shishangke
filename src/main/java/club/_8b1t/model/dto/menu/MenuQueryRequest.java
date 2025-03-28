package club._8b1t.model.dto.menu;

import club._8b1t.model.enums.menu.MenuCategory;
import club._8b1t.model.enums.menu.MenuStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuQueryRequest {

    /**
     * 所属商家ID
     */
    private String merchantId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品分类
     */
    private MenuCategory category;

    /**
     * 菜品状态
     */
    private MenuStatus status;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
} 