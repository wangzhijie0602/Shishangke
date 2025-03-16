package club._8b1t.service;

import club._8b1t.model.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
* @author 8bit
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-03-06 19:59:26
*/
public interface UserService extends IService<User> {
    
    /**
     * 获取用户对象
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUser(Long userId);
    
    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String updateAvatar(Long userId, MultipartFile file);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 更新用户昵称
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 是否成功
     */
    boolean updateNickname(Long userId, String nickname);
    
    /**
     * 更新用户邮箱
     * @param userId 用户ID
     * @param email 新邮箱
     * @return 是否成功
     */
    boolean updateEmail(Long userId, String email);
    
    /**
     * 更新用户电话
     * @param userId 用户ID
     * @param phone 新电话
     * @return 是否成功
     */
    boolean updatePhone(Long userId, String phone);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateStatus(Long userId, String status);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 是否成功
     */
    boolean updateLastLoginTime(Long userId, Date lastLoginTime);
    
    /**
     * 分页获取用户列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param username 用户名(可选)
     * @param nickname 昵称(可选)
     * @param phone 手机号(可选)
     * @param status 状态(可选)
     * @return 用户分页列表
     */
    Page<User> listUsers(int pageNum, int pageSize, String username, String nickname, String phone, String status);
    
    /**
     * 管理员创建用户
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @param email 邮箱
     * @param phone 电话
     * @param role 角色
     * @return 用户ID
     */
    Long createUser(String username, String password, String nickname, String email, String phone, String role);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(User user);
}
