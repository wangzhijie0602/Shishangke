package club._8b1t.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangePassword {

    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
