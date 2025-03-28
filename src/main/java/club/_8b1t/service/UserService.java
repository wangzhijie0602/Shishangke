package club._8b1t.service;

import club._8b1t.model.entity.User;
import club._8b1t.model.enums.User.StatusEnum;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 * 提供用户相关的业务操作，包括用户信息管理、密码管理、状态管理等
 * 
 * @author 8bit
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-03-06 19:59:26
 */
public interface UserService extends IService<User> {

    /**
     * 更新用户头像
     * 将用户上传的头像文件保存到云存储，并更新用户头像URL
     * 
     * @param userId 用户ID，用于标识要更新头像的用户
     * @param file 用户上传的头像文件
     * @return 更新后的头像URL
     */
    String updateAvatar(Long userId, MultipartFile file);
    
    /**
     * 更新用户密码（需要验证旧密码）
     * 验证用户旧密码后，更新为新密码
     * 
     * @param userId 用户ID
     * @param oldPassword 用户输入的旧密码
     * @param newPassword 用户设置的新密码
     * @return 更新是否成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户密码（无需验证旧密码）
     * 直接更新用户密码，通常用于管理员重置密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新是否成功
     */
    boolean updatePassword(Long userId, String newPassword);

    /**
     * 更新用户昵称
     * 修改用户的显示名称
     * 
     * @param userId 用户ID
     * @param nickname 新的昵称
     * @return 更新是否成功
     */
    boolean updateNickname(Long userId, String nickname);
    
    /**
     * 更新用户邮箱
     * 修改用户的联系邮箱
     * 
     * @param userId 用户ID
     * @param email 新的邮箱地址
     * @return 更新是否成功
     */
    boolean updateEmail(Long userId, String email);
    
    /**
     * 更新用户电话
     * 修改用户的联系电话
     * 
     * @param userId 用户ID
     * @param phone 新的电话号码
     * @return 更新是否成功
     */
    boolean updatePhone(Long userId, String phone);
    
    /**
     * 更新用户状态
     * 修改用户的账号状态（如：正常、禁用等）
     * 
     * @param userId 用户ID
     * @param status 新的状态枚举值
     * @return 更新是否成功
     */
    boolean updateStatus(Long userId, StatusEnum status);
    
    /**
     * 分页获取用户列表
     * 根据查询条件获取用户列表，支持模糊查询和精确匹配
     * 
     * @param user 查询参数，包含用户名、昵称、手机号等查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页显示的记录数
     * @return 包含用户列表的分页对象
     */
    Page<User> listUsers(User user, int pageNum, int pageSize);
}
