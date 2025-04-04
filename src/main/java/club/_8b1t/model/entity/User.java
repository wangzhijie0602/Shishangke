package club._8b1t.model.entity;

import club._8b1t.model.enums.User.StatusEnum;
import club._8b1t.model.enums.User.RoleEnum;
import club._8b1t.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
@AutoMapper(target = UserVO.class)
public class User {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 唯一用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号状态
     */
    private StatusEnum status;

    /**
     * 用户角色
     */
    private RoleEnum role;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 软删除
     */
    @TableLogic
    private Integer isDeleted;
}