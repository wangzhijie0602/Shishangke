package club._8b1t.model.dto.request;

import club._8b1t.model.domain.User;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

/**
 * @author 8bit
 */
@Data
@AutoMapper(target = User.class)
public class UserRequest {

    private String username;

    private String email;

    private String phone;

    @AutoMapping(target = "passwordHash")
    private String password;

}
