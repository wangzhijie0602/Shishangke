package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家评价VO
 */
@Data
public class MerchantReviewVO {
    
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
     * 商家ID
     */
    private Integer merchantId;
    
    /**
     * 商家名称
     */
    private String merchantName;
    
    /**
     * 商家Logo
     */
    private String merchantLogo;
    
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
     * 是否匿名
     */
    private Integer isAnonymous;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 