package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.model.dto.UserCreateRequest;
import club._8b1t.model.dto.UserQueryRequest;
import club._8b1t.model.dto.UserUpdateRequest;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static club._8b1t.exception.ErrorCode.PARAMS_ERROR;
import static club._8b1t.exception.ErrorCode.SYSTEM_ERROR;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminController {

    private final UserService userService;
    private final Converter converter;

    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateRequest request) {
        User user = converter.convert(request, User.class);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userService.save(user);
        return ResultUtil.success("创建成功", user.getId());
    }

    @SaCheckRole("ADMIN")
    @GetMapping("/{id}")
    public Result<UserVO> get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }

        // 创建UserRoleVO对象
        UserVO userRoleVO = converter.convert(user, UserVO.class);

        return ResultUtil.success(userRoleVO);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid UserUpdateRequest updateRequest) {
        User user = userService.getById(updateRequest.getId());
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }
        user.setUsername(updateRequest.getUsername());
        user.setPassword(updateRequest.getPassword());
        user.setNickname(updateRequest.getNickname());
        user.setEmail(updateRequest.getEmail());
        user.setPhone(updateRequest.getPhone());
        userService.updateById(user);
        return ResultUtil.success("更新成功");
    }

    @GetMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 获取用户的角色列表
     *
     * @param id 用户ID
     * @return 用户的角色列表
     */
    @GetMapping("/{id}/role")
    public Result<String> getRole(@PathVariable Long id) {
        // 根据用户ID查询用户角色关系

        String role = userService.getObj(new LambdaQueryWrapper<>(User.class)
                .select(User::getRole)
                .eq(User::getId, id), Object::toString);

        // 如果用户没有角色，则返回空列表
        if (StrUtil.isBlank(role)) {
            throw new BusinessException(SYSTEM_ERROR, "用户没有角色");
        }

        // 返回角色编码列表
        return ResultUtil.success("获取角色成功", role);
    }

    @SaCheckRole("ADMIN")
    @GetMapping("/{id}/ban")
    public Result<Void> ban(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }

        user.setStatus("DISABLED");
        userService.updateById(user);

        return ResultUtil.success("禁用成功");
    }

    @GetMapping("/{id}/unban")
    public Result<Void> unban(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }
        user.setStatus("ENABLED");
        userService.updateById(user);
        return ResultUtil.success("解封成功");
    }

    @PostMapping("/list")
    public Result<IPage<User>> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestBody(required = false) UserQueryRequest request) {

        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果请求参数不为空，则添加查询条件
        if (request != null) {
            // 如果用户名不为空，则添加用户名查询条件
            userLambdaQueryWrapper.like(StrUtil.isNotBlank(request.getUsername()), User::getUsername, request.getUsername());
            // 如果手机号不为空，则添加手机号查询条件
            userLambdaQueryWrapper.like(StrUtil.isNotBlank(request.getPhone()), User::getPhone, request.getPhone());
            // 如果昵称不为空，则添加昵称查询条件
            userLambdaQueryWrapper.like(StrUtil.isNotBlank(request.getNickname()), User::getNickname, request.getNickname());
            // 如果状态不为空，则添加状态查询条件
            userLambdaQueryWrapper.eq(StrUtil.isNotBlank(request.getStatus()), User::getStatus, request.getStatus());
        }
        // 创建一个Page对象，用于分页查询
        Page<User> page = new Page<>(pageNum, pageSize);

        // 调用userService的page方法，执行分页查询，并返回结果
        return ResultUtil.success(userService.page(page, userLambdaQueryWrapper));
    }

}
