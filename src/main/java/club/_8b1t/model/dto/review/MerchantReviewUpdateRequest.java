package club._8b1t.model.dto.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家评价更新请求
 */
@Data
public class MerchantReviewUpdateRequest {
    
    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评分（1.0 ~ 5.0）
     */
    @DecimalMin(value = "1.0", message = "评分最低为1.0")
    @DecimalMax(value = "5.0", message = "评分最高为5.0")
    private BigDecimal rating;
    
    /**
     * 评价图片URL列表
     */
    private List<String> images;
    
    /**
     * 是否匿名（0:否，1:是）
     */
    private Integer isAnonymous;
} 