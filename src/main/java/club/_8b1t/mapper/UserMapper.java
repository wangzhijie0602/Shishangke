package club._8b1t.mapper;

import club._8b1t.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 8bit
 * @version 1.0
 * @since 1.0
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> getAllUsers();

}
