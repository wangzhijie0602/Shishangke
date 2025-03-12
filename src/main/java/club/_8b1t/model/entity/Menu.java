package club._8b1t.model.entity;

import club._8b1t.model.vo.MenuVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 菜单表
 * @TableName menu
 */
@TableName(value ="menu")
@Data
@AutoMapper(target = MenuVO.class)
public class Menu {
    /**
     * 菜单项ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer menuId;

    /**
     * 所属商家ID（外键关联 merchant.id）
     */
    private Long merchantId;

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

    /**
     * 软删除标记
     */
    @TableLogic
    private Integer isDeleted;
}