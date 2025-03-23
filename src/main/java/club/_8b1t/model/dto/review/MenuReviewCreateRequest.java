package club._8b1t.model.dto.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 菜品评价创建请求
 */
@Data
public class MenuReviewCreateRequest {
    
    /**
     * 菜品ID
     */
    @NotNull(message = "菜品ID不能为空")
    private Integer menuId;
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评分（1.0 ~ 5.0）
     */
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分最低为1.0")
    @DecimalMax(value = "5.0", message = "评分最高为5.0")
    private BigDecimal rating;
    
    /**
     * 评价图片URL列表
     */
    private List<String> images;
    
    /**
     * 口味评分
     */
    @DecimalMin(value = "1.0", message = "口味评分最低为1.0")
    @DecimalMax(value = "5.0", message = "口味评分最高为5.0")
    private BigDecimal tasteRating;
    
    /**
     * 外观评分
     */
    @DecimalMin(value = "1.0", message = "外观评分最低为1.0")
    @DecimalMax(value = "5.0", message = "外观评分最高为5.0")
    private BigDecimal appearanceRating;
    
    /**
     * 是否匿名（0:否，1:是）
     */
    private Integer isAnonymous = 0;
} 