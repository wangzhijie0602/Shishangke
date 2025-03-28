package club._8b1t.model.dto.user;

import club._8b1t.model.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = User.class)
public class QueryRequest {
    private String username;
    private String phone;
    private String nickname;
    private String status;
}
