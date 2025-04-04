package club._8b1t.model.dto.menu;

import club._8b1t.model.entity.Menu;
import club._8b1t.model.enums.menu.MenuCategory;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AutoMapper(target = Menu.class)
public class MenuCreateRequest {

    /**
     * 所属商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private String merchantId;

    /**
     * 菜品名称
     */
    @NotBlank(message = "菜品名称不能为空")
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 菜品价格（单位：元）
     */
    @NotNull(message = "菜品价格不能为空")
    private BigDecimal price;

    /**
     * 菜品分类
     */
    private MenuCategory category;

    /**
     * 菜品图片URL
     */
    private String imageUrl;

    /**
     * 菜品状态
     */
    private String status;

    /**
     * 排序权重（数值越小越靠前）
     */
    private Integer sortOrder;
} 