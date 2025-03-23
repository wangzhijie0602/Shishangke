package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商家评价实体类
 * @TableName merchant_review
 */
@Data
@TableName("merchant_review")
public class MerchantReview {
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
     * 商家ID（外键关联 merchant.id）
     */
    private Integer merchantId;

    /**
     * 评价内容（文字评论）
     */
    private String content;

    /**
     * 评分（1.0 ~ 5.0）
     */
    private BigDecimal rating;

    /**
     * 评价图片URL（JSON数组格式，如["url1", "url2"]）
     */
    private String images;

    /**
     * 是否匿名（0: 否，1: 是）
     */
    private Integer isAnonymous;

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