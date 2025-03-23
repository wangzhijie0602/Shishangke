package club._8b1t.model.dto.customer;

import club._8b1t.model.entity.Customer;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 顾客创建请求
 */
@Data
@AutoMapper(target = Customer.class)
public class CustomerCreateRequest {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别(MALE:男, FEMALE:女, OTHER:其他)
     */
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "性别值只能是MALE、FEMALE或OTHER")
    private String gender;
    
    /**
     * 出生日期 (格式：yyyy-MM-dd)
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出生日期格式必须为yyyy-MM-dd")
    private String birthDate;
    
    /**
     * 饮食偏好（JSON格式）
     */
    private String preferences;
} 