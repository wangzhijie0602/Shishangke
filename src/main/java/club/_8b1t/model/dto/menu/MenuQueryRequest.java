package club._8b1t.model.dto.menu;

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
    private String category;

    /**
     * 菜品状态
     */
    private String status;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
} 