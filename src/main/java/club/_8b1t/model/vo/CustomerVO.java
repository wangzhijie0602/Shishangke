package club._8b1t.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 顾客VO
 */
@Data
public class CustomerVO {
    
    /**
     * 顾客ID
     */
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    
    /**
     * 饮食偏好
     */
    private String preferences;
    
    /**
     * VIP等级
     */
    private Integer vipLevel;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 