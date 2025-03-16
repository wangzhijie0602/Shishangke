package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 菜品评价表
 * @TableName menu_review
 */
@TableName(value ="menu_review")
@Data
public class MenuReview {
    /**
     * 评价ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long reviewId;

    /**
     * 评价用户ID（外键关联 user.id）
     */
    private Long userId;

    /**
     * 菜品ID（外键关联 menu.menu_id）
     */
    private Integer menuId;

    /**
     * 关联订单ID（外键关联 order.order_id）
     */
    private Long orderId;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评分（1.0 ~ 5.0）
     */
    private BigDecimal rating;

    /**
     * 评价图片URL（JSON数组格式）
     */
    private String images;

    /**
     * 口味评分
     */
    private BigDecimal tasteRating;

    /**
     * 外观评分
     */
    private BigDecimal appearanceRating;

    /**
     * 是否匿名（0:否，1:是）
     */
    private Integer isAnonymous;

    /**
     * 点赞数量
     */
    private Integer likesCount;

    /**
     * 评价创建时间
     */
    private Date createdAt;

    /**
     * 最后更新时间
     */
    private Date updatedAt;

    /**
     * 软删除标记
     */
    private Integer isDeleted;
}