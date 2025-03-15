package club._8b1t.model.dto.user;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 用户信息更新请求DTO
 * 普通用户只能更新自己的昵称、邮箱和电话号码
 */
@AutoMapper(target = User.class)
@Data
public class UserInfoUpdateRequest {
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 电话号码
     */
    private String phone;
} 