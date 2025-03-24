package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.dto.customer.CustomerCreateRequest;
import club._8b1t.model.dto.customer.CustomerLoginRequest;
import club._8b1t.model.dto.customer.address.CustomerAddressCreateRequest;
import club._8b1t.model.dto.customer.address.CustomerAddressUpdateRequest;
import club._8b1t.model.entity.Customer;
import club._8b1t.model.entity.CustomerAddress;
import club._8b1t.model.entity.Menu;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.vo.CustomerAddressVO;
import club._8b1t.model.vo.CustomerVO;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.CustomerAddressService;
import club._8b1t.service.CustomerService;
import club._8b1t.service.MenuService;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@Slf4j
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @Resource
    private Converter converter;

    @Resource
    private MerchantService merchantService;

    @Resource
    private MenuService menuService;
    
    @Resource
    private CustomerAddressService customerAddressService;

    /**
     * 注册顾客账号
     */
    @PostMapping("/register")
    public Result<String> registerCustomer(@RequestBody @Valid CustomerCreateRequest request) {
        // 检查用户名是否已存在
        Customer existingCustomer = customerService.getCustomerByUsername(request.getUsername());
        if (existingCustomer != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }

        // 将请求转换为顾客实体
        Customer customer = converter.convert(request, Customer.class);

        // 创建顾客信息
        Long customerId = customerService.createCustomer(customer);

        return ResultUtil.success("注册成功", customerId.toString());
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<CustomerVO> login(@RequestBody @Valid CustomerLoginRequest request) {
        log.info("用户登录：username={}", request.getUsername());
        // 登录验证
        Customer customer = customerService.getCustomerByUsernameAndPassword(request.getUsername(), request.getPassword());
        if (customer == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }

        // 使用Sa-Token登录，记录用户ID
        StpUtil.login(customer.getId(), true);
        log.info("Sa-Token登录成功，Token信息：{}", StpUtil.getTokenInfo());

        // 转换为VO
        CustomerVO customerVO = converter.convert(customer, CustomerVO.class);

        return ResultUtil.success("登录成功", customerVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return ResultUtil.success("退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<CustomerVO> current() {

        // 获取登录用户ID
        String customerId = StpUtil.getLoginIdAsString();

        // 获取用户信息
        Customer customer = customerService.getById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户信息不存在");
        }

        // 转换为VO
        CustomerVO customerVO = converter.convert(customer, CustomerVO.class);

        return ResultUtil.success(customerVO);
    }

    /**
     * 更新顾客真实姓名
     */
    @PostMapping("/update/nickname")
    public Result<String> updateRealName(@RequestParam String nickname) {

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();

        // 检查顾客信息是否存在
        Customer existingCustomer = customerService.getById(customerId);
        if (existingCustomer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "顾客信息不存在");
        }

        // 更新真实姓名
        boolean updated = customerService.updateNickname(customerId, nickname);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }

        return ResultUtil.success("更新成功");
    }

    /**
     * 更新顾客性别
     */
    @PostMapping("/update/gender")
    public Result<String> updateGender(@RequestParam String gender) {

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();

        // 检查顾客信息是否存在
        Customer existingCustomer = customerService.getById(customerId);
        if (existingCustomer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "顾客信息不存在");
        }

        // 验证性别值
        if (!gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("OTHER")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "性别值无效");
        }

        // 更新性别
        boolean updated = customerService.updateGender(customerId, gender);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }

        return ResultUtil.success("更新成功");
    }

    /**
     * 更新顾客出生日期
     */
    @PostMapping("/update/birthdate")
    public Result<String> updateBirthDate(@RequestParam String birthDate) {

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();

        // 检查顾客信息是否存在
        Customer existingCustomer = customerService.getById(customerId);
        if (existingCustomer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "顾客信息不存在");
        }

        // 更新出生日期
        boolean updated = customerService.updateBirthDate(customerId, birthDate);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }

        return ResultUtil.success("更新成功");
    }

    /**
     * 更新顾客饮食偏好
     */
    @PostMapping("/update/preferences")
    public Result<String> updatePreferences(@RequestParam String preferences) {

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();

        // 检查顾客信息是否存在
        Customer existingCustomer = customerService.getById(customerId);
        if (existingCustomer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "顾客信息不存在");
        }

        // 更新饮食偏好
        boolean updated = customerService.updatePreferences(customerId, preferences);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }

        return ResultUtil.success("更新成功");
    }

    /**
     * 更新顾客头像
     */
    @PostMapping("/update/avatar")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();

        // 检查顾客信息是否存在
        Customer existingCustomer = customerService.getById(customerId);
        if (existingCustomer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "顾客信息不存在");
        }

        // 上传头像并更新
        String avatarUrl = customerService.updateAvatar(customerId, file);
        if (avatarUrl == null) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "头像上传失败");
        }

        return ResultUtil.success("头像上传成功", avatarUrl);
    }

    @GetMapping("/merchants")
    public Result<Page<MerchantVO>> getMerchants(@RequestParam(defaultValue = "1") Integer pageNumber,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Merchant> merchantPage = merchantService.getMerchantPage(pageNumber, pageSize);

        Page<MerchantVO> merchantVOPage = new Page<>(merchantPage.getCurrent(), merchantPage.getSize(), merchantPage.getTotal());

        merchantVOPage.setRecords(converter.convert(merchantPage.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOPage);

    }

    @GetMapping("/merchant/{merchantId}")
    public Result<MerchantVO> getMerchant(@PathVariable String merchantId) {

        Merchant merchant = merchantService.getById(merchantId);

        if (merchant == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "餐厅不存在");
        }

        MerchantVO merchantVO = converter.convert(merchant, MerchantVO.class);

        return ResultUtil.success(merchantVO);

    }

    @GetMapping("/menu/{merchantId}")
    public Result<List<Menu>> getMerchantMenus(@PathVariable String merchantId) {

        List<Menu> menuList = menuService.getMenuByMerchantId(Long.parseLong(merchantId));

        return ResultUtil.success(menuList);

    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/address/list")
    public Result<List<CustomerAddressVO>> getAddressList() {
        // 获取当前登录用户ID
        String customerId = StpUtil.getLoginIdAsString();
        
        // 获取地址列表
        List<CustomerAddress> addressList = customerAddressService.getAddressList(Long.parseLong(customerId));
        
        // 转换为VO
        List<CustomerAddressVO> addressVOList = converter.convert(addressList, CustomerAddressVO.class);
        
        return ResultUtil.success(addressVOList);
    }
    
    /**
     * 获取用户默认地址
     */
    @GetMapping("/address/default")
    public Result<CustomerAddressVO> getDefaultAddress() {
        // 获取当前登录用户ID
        String customerId = StpUtil.getLoginIdAsString();
        
        // 获取默认地址
        CustomerAddress defaultAddress = customerAddressService.getDefaultAddress(Long.parseLong(customerId));
        if (defaultAddress == null) {
            return ResultUtil.success("未设置默认地址", null);
        }
        
        // 转换为VO
        CustomerAddressVO addressVO = converter.convert(defaultAddress, CustomerAddressVO.class);
        
        return ResultUtil.success(addressVO);
    }
    
    /**
     * 添加新地址
     */
    @PostMapping("/address/add")
    public Result<String> addAddress(@RequestBody @Valid CustomerAddressCreateRequest request) {
        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        
        // 将请求转换为实体
        CustomerAddress address = converter.convert(request, CustomerAddress.class);
        
        // 设置用户ID
        address.setCustomerId(customerId);
        
        // 添加地址
        boolean added = customerAddressService.addAddress(address);
        if (!added) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "添加地址失败");
        }
        
        return ResultUtil.success("添加地址成功");
    }
    
    /**
     * 更新地址信息
     */
    @PostMapping("/address/update")
    public Result<String> updateAddress(@RequestBody @Valid CustomerAddressUpdateRequest request) {
        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        
        // 验证地址所有权
        CustomerAddress existingAddress = customerAddressService.getById(request.getId());
        if (existingAddress == null || !existingAddress.getCustomerId().equals(customerId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "地址不存在或无权修改");
        }
        
        // 将请求转换为实体
        CustomerAddress address = converter.convert(request, CustomerAddress.class);
        
        // 设置用户ID
        address.setCustomerId(customerId);
        
        // 更新地址
        boolean updated = customerAddressService.updateAddress(address);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新地址失败");
        }
        
        return ResultUtil.success("更新地址成功");
    }
    
    /**
     * 设置默认地址
     */
    @PostMapping("/address/set-default/{addressId}")
    public Result<String> setDefaultAddress(@PathVariable String addressId) {
        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        
        // 验证地址所有权
        CustomerAddress existingAddress = customerAddressService.getById(addressId);
        if (existingAddress == null || !existingAddress.getCustomerId().equals(customerId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "地址不存在或无权修改");
        }
        
        // 设置默认地址
        boolean set = customerAddressService.setDefaultAddress(Long.parseLong(addressId), customerId);
        if (!set) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "设置默认地址失败");
        }
        
        return ResultUtil.success("设置默认地址成功");
    }
    
    /**
     * 删除地址
     */
    @PostMapping("/address/delete/{addressId}")
    public Result<String> deleteAddress(@PathVariable String addressId) {
        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        
        // 验证地址所有权
        CustomerAddress existingAddress = customerAddressService.getById(addressId);
        if (existingAddress == null || !existingAddress.getCustomerId().equals(customerId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "地址不存在或无权删除");
        }
        
        boolean deleted = customerAddressService.removeById(existingAddress);
        if (!deleted) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "删除地址失败");
        }
        
        return ResultUtil.success("删除地址成功");
    }

} 