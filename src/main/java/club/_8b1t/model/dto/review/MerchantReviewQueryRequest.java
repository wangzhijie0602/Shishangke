package club._8b1t.model.dto.review;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家评价查询请求
 */
@Data
public class MerchantReviewQueryRequest {
    
    /**
     * 商家ID
     */
    private Integer merchantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 最低评分
     */
    private BigDecimal minRating;
    
    /**
     * 最高评分
     */
    private BigDecimal maxRating;
    
    /**
     * 是否匿名
     */
    private Integer isAnonymous;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 