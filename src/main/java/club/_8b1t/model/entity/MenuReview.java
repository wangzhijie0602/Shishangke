package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 菜品评价实体类
 * @TableName menu_review
 */
@Data
@TableName("menu_review")
public class MenuReview {
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long reviewId;

    /**
     * 评价用户ID
     */
    private Long userId;

    /**
     * 菜品ID
     */
    private Integer menuId;

    /**
     * 关联订单ID
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
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;
}