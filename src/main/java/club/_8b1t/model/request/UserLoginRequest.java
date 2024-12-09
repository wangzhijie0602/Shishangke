package club._8b1t.model.request;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录包装类
 *
 * @author 8bit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = User.class)
public class UserLoginRequest {

    private String username;
    private String email;

    @NotBlank(message = "密码不能为空,请输入密码")
    private String password;
}
