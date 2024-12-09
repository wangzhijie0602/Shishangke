package club._8b1t.model.entity;

import club._8b1t.model.enums.Role;
import club._8b1t.model.enums.Status;
import club._8b1t.model.response.UserInfoResponse;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 用户表实体类
 *
 * @author 8bit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = UserInfoResponse.class)
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private Role role;
    private Status status;

    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Short isDeleted;
}


