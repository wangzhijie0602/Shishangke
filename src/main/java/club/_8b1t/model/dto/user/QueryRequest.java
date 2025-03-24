package club._8b1t.model.dto.user;

import lombok.Data;

@Data
public class QueryRequest {
    private String username;
    private String phone;
    private String nickname;
    private String status;
}
