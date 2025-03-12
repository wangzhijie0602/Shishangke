package club._8b1t.model.dto.menu;

import club._8b1t.model.entity.Menu;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AutoMapper(target = Menu.class)
public class MenuUpdateRequest {

    /**
     * 菜单项ID，主键
     */
    @NotNull(message = "菜单ID不能为空")
    private String menuId;

    /**
     * 所属商家ID
     */
    private String merchantId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 菜品价格（单位：元）
     */
    private BigDecimal price;

    /**
     * 菜品分类
     */
    private String category;

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