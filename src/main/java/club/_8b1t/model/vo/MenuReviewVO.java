package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品评价VO
 */
@Data
public class MenuReviewVO {
    
    /**
     * 评价ID
     */
    private Long reviewId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 菜品ID
     */
    private Integer menuId;
    
    /**
     * 菜品名称
     */
    private String menuName;
    
    /**
     * 菜品图片
     */
    private String menuImageUrl;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评价图片URL列表
     */
    private List<String> images;
    
    /**
     * 口味评分
     */
    private BigDecimal tasteRating;
    
    /**
     * 外观评分
     */
    private BigDecimal appearanceRating;
    
    /**
     * 是否匿名
     */
    private Integer isAnonymous;
    
    /**
     * 点赞数量
     */
    private Integer likesCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 