package club._8b1t.model.dto.user;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AutoMapper(target = User.class)
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    private String email;

    private String phone;
}
