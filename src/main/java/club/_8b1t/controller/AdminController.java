package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.dto.merchant.MerchantUpdateRequest;
import club._8b1t.model.dto.user.UserCreateRequest;
import club._8b1t.model.dto.user.UserQueryRequest;
import club._8b1t.model.dto.user.UserUpdateRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.MerchantService;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin")
@SaCheckRole("ADMIN")
public class AdminController {

    @Resource
    private UserService userService;
    
    @Resource
    private MerchantService merchantService;

    @Resource
    private Converter converter;

    @PostMapping("/user")
    public Result<Long> create(@RequestBody @Valid UserCreateRequest request) {
        // 创建用户
        Long userId = userService.createUser(
                request.getUsername(), 
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                request.getPhone(),
                "USER" // 默认为普通用户角色
        );
        
        return ResultUtil.success("创建成功", userId);
    }

    @GetMapping("/user/{id}")
    public Result<UserVO> get(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        
        // 获取用户信息
        User user = userService.getUser(userId);
        
        // 转换为VO
        UserVO userVO = converter.convert(user, UserVO.class);
        
        return ResultUtil.success(userVO);
    }

    @PostMapping("/user/update")
    public Result<Void> update(@RequestBody @Valid UserUpdateRequest updateRequest) {
        // 创建User对象
        User user = converter.convert(updateRequest, User.class);
        
        // 更新用户信息
        boolean success = userService.updateUserInfo(user);
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("更新成功");
    }

    @GetMapping("/user/{id}/delete")
    public Result<Void> delete(@PathVariable String id) {
        boolean success = userService.removeById(id);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtil.success("删除成功");
    }

    /**
     * 获取用户的角色
     * @param id 用户ID
     * @return 用户的角色
     */
    @GetMapping("/user/{id}/role")
    public Result<String> getRole(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        
        // 获取用户信息
        User user = userService.getUser(userId);
        
        // 如果用户没有角色，则抛出异常
        if (StrUtil.isBlank(user.getRole())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户没有角色");
        }
        
        // 返回角色
        return ResultUtil.success("获取角色成功", user.getRole());
    }

    @GetMapping("/user/{id}/ban")
    public Result<Void> ban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        
        // 禁用用户
        boolean success = userService.updateStatus(userId, "DISABLED");
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "禁用失败");
        }
        
        return ResultUtil.success("禁用成功");
    }

    @GetMapping("/user/{id}/unban")
    public Result<Void> unban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        
        // 解封用户
        boolean success = userService.updateStatus(userId, "ENABLED");
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解封失败");
        }
        
        return ResultUtil.success("解封成功");
    }

    @PostMapping("/user/list")
    public Result<Page<UserVO>> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestBody(required = false) UserQueryRequest request) {
        // 构建查询参数
        String username = request != null ? request.getUsername() : null;
        String nickname = request != null ? request.getNickname() : null;
        String phone = request != null ? request.getPhone() : null;
        String status = request != null ? request.getStatus() : null;
        
        // 查询用户列表
        Page<User> userPage = userService.listUsers(pageNum, pageSize, username, nickname, phone, status);
        
        // 将Page<User>转换为Page<UserVO>
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        userVOPage.setRecords(converter.convert(userPage.getRecords(), UserVO.class));
        
        return ResultUtil.success(userVOPage);
    }

    /**
     * 管理员查询商家列表
     */
    @PostMapping("/merchant/list")
    public Result<Page<MerchantVO>> getMerchantList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestBody(required = false) MerchantQueryRequest request) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        
        if (request != null) {
            // 根据用户ID筛选
            wrapper.eq(request.getUserId() != null, Merchant::getUserId, request.getUserId());
            // 根据商家名称模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getName()), Merchant::getName, request.getName());
            // 根据商家电话模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getPhone()), Merchant::getPhone, request.getPhone());
            // 根据状态筛选
            wrapper.eq(StrUtil.isNotBlank(request.getStatus()), Merchant::getStatus, request.getStatus());
        }
        
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);
        
        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));
        
        return ResultUtil.success(merchantVOList);
    }
    
    /**
     * 管理员获取商家详情
     */
    @GetMapping("/merchant/{id}")
    public Result<MerchantVO> getMerchant(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        return ResultUtil.success(converter.convert(merchant, MerchantVO.class));
    }
    
    /**
     * 管理员完整更新商家信息
     */
    @PostMapping("/merchant/update")
    public Result<String> updateMerchant(@RequestBody @Valid MerchantUpdateRequest request) {
        // 验证商家是否存在
        Merchant merchant = merchantService.getById(request.getId());
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        // 将请求参数转换为商家对象
        Merchant updatedMerchant = converter.convert(request, Merchant.class);
        
        // 更新商家信息
        boolean updated = merchantService.updateMerchantInfo(updatedMerchant);
        
        // 如果更新失败，抛出系统异常
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("更新成功");
    }
    
    /**
     * 管理员更新商家名称
     */
    @PostMapping("/merchant/{id}/update-name")
    public Result<String> updateMerchantName(@PathVariable Long id, @RequestParam String name) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateName(id, name);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("名称更新成功");
    }
    
    /**
     * 管理员更新商家Logo
     */
    @PostMapping("/merchant/{id}/update-logo")
    public Result<String> updateMerchantLogo(@PathVariable Long id, @RequestParam String logo) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateLogo(id, logo);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("Logo更新成功");
    }
    
    /**
     * 管理员更新商家联系电话
     */
    @PostMapping("/merchant/{id}/update-phone")
    public Result<String> updateMerchantPhone(@PathVariable Long id, @RequestParam String phone) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updatePhone(id, phone);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("联系电话更新成功");
    }
    
    /**
     * 管理员更新商家地址
     */
    @PostMapping("/merchant/{id}/update-address")
    public Result<String> updateMerchantAddress(@PathVariable Long id, 
                                              @RequestParam String province,
                                              @RequestParam String city,
                                              @RequestParam String district,
                                              @RequestParam String street,
                                              @RequestParam String addressDetail) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateAddress(id, province, city, district, street, addressDetail);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("地址更新成功");
    }
    
    /**
     * 管理员更新商家营业时间
     */
    @PostMapping("/merchant/{id}/update-business-hours")
    public Result<String> updateMerchantBusinessHours(@PathVariable Long id, 
                                                    @RequestParam String openTime,
                                                    @RequestParam String closeTime) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateBusinessHours(id, openTime, closeTime);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("营业时间更新成功");
    }
    
    /**
     * 管理员更新商家描述
     */
    @PostMapping("/merchant/{id}/update-description")
    public Result<String> updateMerchantDescription(@PathVariable Long id, @RequestParam String description) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateDescription(id, description);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("描述更新成功");
    }
    
    /**
     * 管理员更新商家最低起送价
     */
    @PostMapping("/merchant/{id}/update-min-price")
    public Result<String> updateMerchantMinPrice(@PathVariable Long id, @RequestParam BigDecimal minPrice) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateMinPrice(id, minPrice);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("最低起送价更新成功");
    }
    
    /**
     * 管理员更新商家状态
     */
    @PostMapping("/merchant/{id}/update-status")
    public Result<String> updateMerchantStatus(@PathVariable Long id, @RequestParam String status) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean updated = merchantService.updateStatus(id, status);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }

        return ResultUtil.success("状态更新成功");
    }
    
    /**
     * 管理员删除商家
     */
    @GetMapping("/merchant/{id}/delete")
    public Result<String> deleteMerchant(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在");
        }
        
        boolean deleted = merchantService.removeById(id);
        if (!deleted) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        
        return ResultUtil.success("删除成功");
    }
}
