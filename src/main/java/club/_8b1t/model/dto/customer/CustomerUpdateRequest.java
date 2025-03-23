package club._8b1t.model.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 顾客更新请求
 */
@Data
public class CustomerUpdateRequest {
    
    /**
     * 用户名，用于标识要更新的用户
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
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
    @Pattern(regexp = "^(男|女)?$", message = "性别值只能是男、女")
    private String gender;
    
    /**
     * 出生日期 (格式：yyyy-MM-dd)
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$|^$", message = "出生日期格式必须为yyyy-MM-dd")
    private String birthDate;
    
    /**
     * 饮食偏好（JSON格式）
     */
    private String preferences;
} 