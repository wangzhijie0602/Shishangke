package club._8b1t.model.dto.customer;

import lombok.Data;

/**
 * 顾客查询请求
 */
@Data
public class CustomerQueryRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * VIP等级最小值
     */
    private Integer minVipLevel;
    
    /**
     * VIP等级最大值
     */
    private Integer maxVipLevel;
    
    /**
     * 积分最小值
     */
    private Integer minPoints;
    
    /**
     * 积分最大值
     */
    private Integer maxPoints;
} 