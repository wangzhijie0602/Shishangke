package club._8b1t.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String status;
    private String role;
    private Date lastLoginTime;
    private Date createdAt;
}
