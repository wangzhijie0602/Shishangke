package club._8b1t.model.dto.user;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@AutoMapper(target = User.class)
@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private String id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String email;

    private String phone;
}
