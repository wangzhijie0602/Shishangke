package club._8b1t.model.request;

import club._8b1t.model.entity.User;
import club._8b1t.model.enums.Role;
import club._8b1t.model.enums.Status;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户管理请求类
 *
 * @author 8bit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = User.class)
public class UserManagerRequest {

    @NotNull
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private Role role;
    private Status status;

}
