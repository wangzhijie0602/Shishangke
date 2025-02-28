package club._8b1t.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Object status;
    private Date lastLoginTime;
    private Date createTime;
}
