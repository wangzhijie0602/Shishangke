package club._8b1t.model.request;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册包装类
 *
 * @author 8bit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = User.class)
public class UserRegisterRequest {

//    用户名长度在3~50之间
//    允许使用字母、数字、下划线和连字符
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "用户名不合法")
    private String username;

//    密码正则（密码格式应为8-18位数字、字母、符号的任意两种组合
    @NotBlank
    @Pattern(regexp = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[()])+$)(?!^.*[\\u4E00-\\u9FA5].*$)([^(0-9a-zA-Z)]|[()]|[a-z]|[A-Z]|[0-9]){8,255}$", message = "密码不合法")
    private String password;

    @NotBlank
    @Email(message = "邮箱不合法")
    private String email;

}