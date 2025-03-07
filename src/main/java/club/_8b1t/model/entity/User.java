package club._8b1t.model.entity;

import club._8b1t.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String status;

    /**
     * 
     */
    private String role;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 软删除
     */
    @TableLogic
    private Integer isDeleted;
}