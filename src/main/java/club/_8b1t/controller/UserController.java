package club._8b1t.controller;

import club._8b1t.model.domain.User;
import club._8b1t.model.dto.request.UserRequest;
import club._8b1t.model.dto.response.BaseResult;
import club._8b1t.model.validation.group.UserLogin;
import club._8b1t.model.validation.group.UserRegister;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 8bit
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Converter converter;

    @PostMapping("/login")
    public BaseResult<String> login(@RequestBody @Validated(UserLogin.class) UserRequest userRequest) {

        User user = userService.findByUsername(userRequest.getUsername());
        if (user == null) {
            return ResultUtil.error("用户名不存在");
        }
        if (!BCrypt.checkpw(userRequest.getPassword(), user.getPasswordHash())) {
            return ResultUtil.error("密码错误");
        }

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        return ResultUtil.success(token);

    }

    @PostMapping("/register")
    public BaseResult<?> register(@RequestBody @Validated(UserRegister.class) UserRequest userRequest) {

        User convert = converter.convert(userRequest, User.class);
        convert.setPasswordHash(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
        userService.save(convert);

        return ResultUtil.success(convert.getId());
    }

}
