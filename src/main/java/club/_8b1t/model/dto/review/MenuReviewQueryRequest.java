package club._8b1t.model.dto.review;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 菜品评价查询请求
 */
@Data
public class MenuReviewQueryRequest {
    
    /**
     * 菜品ID
     */
    private Integer menuId;
    
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