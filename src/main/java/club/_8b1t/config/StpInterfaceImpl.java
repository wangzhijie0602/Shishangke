package club._8b1t.config;

import club._8b1t.model.entity.User;
import club._8b1t.service.UserService;
import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@RequiredArgsConstructor
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        // 将loginId转换为Long类型
        Long userId = Long.parseLong(loginId.toString());
        // 根据userId查询用户角色
        String role = userService.getObj(new LambdaQueryWrapper<>(User.class)
                .select(User::getRole)
                .eq(User::getId, userId), Object::toString);
        // 返回角色集合
        return List.of(role);

    }

}
