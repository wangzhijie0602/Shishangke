package club._8b1t.service;

import club._8b1t.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 8bit
 * @version 1.0
 * @since 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 查看所有用户信息
     *
     * @param id 用户的id
     * @return 所有用户信息的列表
     * */
    List<User> getAllUsers(long id) throws Exception;

    /**
     * 查看当前用户信息
     *
     * @param id 用户的id
     * @return 用户的信息
     * */
    User getUserInfo(long id) throws Exception;

    /**
     * 用户登录
     *
     * @param user 用户的信息,包含username,password,email
     * @return 用户登录信息
     * */
    User userLogin(User user) throws Exception;

    /**
     * 注册用户
     *
     * @param user 用户的信息,包含username,password,email
     * @return 注册是否成功
     * */
    long userRegister(User user) throws Exception;

    boolean userUpdate(User user) throws Exception;
}
