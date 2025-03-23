package club._8b1t.model.entity;

import club._8b1t.model.vo.CustomerVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 顾客实体类
 */
@Data
@TableName("customer")
@AutoMapper(target = CustomerVO.class)
public class Customer {
    
    /**
     * 顾客ID，主键，自增
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别(MALE:男, FEMALE:女, OTHER:其他)
     */
    private String gender;
    
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    
    /**
     * 饮食偏好（JSON格式）
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
    
    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;
}