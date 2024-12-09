package club._8b1t.controller;

import club._8b1t.model.entity.User;
import club._8b1t.model.request.UserManagerRequest;
import club._8b1t.model.response.BaseResult;
import club._8b1t.model.response.UserInfoResponse;
import club._8b1t.service.UserService;
import club._8b1t.utils.JwtUtil;
import club._8b1t.utils.ResultUtil;
import club._8b1t.utils.ThreadLocalUtil;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对用户各种操作的api接口实现
 *
 * @author 8bit
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Converter converter;

    @GetMapping("/user/all")
    public BaseResult getAllUsers() throws Exception {
        String token = ThreadLocalUtil.getToken();
        long id = JwtUtil.extractUserId(token);
        List<User> userList = userService.getAllUsers(id);
        List<UserInfoResponse> userInfoResponseList = converter.convert(userList, UserInfoResponse.class);

        if (userList.isEmpty()) {
            return ResultUtil.error("用户权限不足");
        }

        return ResultUtil.success(userInfoResponseList);
    }

    @GetMapping("/current")
    public BaseResult userInfo() throws Exception {
        String token = ThreadLocalUtil.getToken();
        long id = JwtUtil.extractUserId(token);
        User user = userService.getUserInfo(id);
        UserInfoResponse userInfoResponse = converter.convert(user, UserInfoResponse.class);
        return ResultUtil.success(userInfoResponse);
    }

    @PostMapping("/user/update")
    public BaseResult modifyUser(@RequestBody UserManagerRequest userManagerRequest) throws Exception {
        User user = converter.convert(userManagerRequest, User.class);
        System.out.println(userManagerRequest.toString());
        System.out.println(user.toString());
        boolean b = userService.userUpdate(user);

        return b ? ResultUtil.success() : ResultUtil.error("更新失败");
    }
}
