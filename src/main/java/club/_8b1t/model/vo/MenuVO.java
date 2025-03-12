package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MenuVO {

    /**
     * 菜单项ID
     */
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

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
} 