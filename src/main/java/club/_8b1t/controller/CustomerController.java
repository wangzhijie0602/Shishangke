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
import club._8b1t.model.enums.menu.MenuStatus;
import club._8b1t.model.enums.merchant.StatusEnum;
import club._8b1t.model.vo.CustomerAddressVO;
import club._8b1t.model.vo.CustomerVO;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.CustomerAddressService;
import club._8b1t.service.CustomerService;
import club._8b1t.service.MenuService;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedissonClient redisson;

    /**
     * 注册顾客账号
     */
    @PostMapping("/register")
    @Operation(operationId = "customer_register")
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
    @Operation(operationId = "customer_login")
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
    @Operation(operationId = "customer_logout")
    public Result<String> logout() {
        StpUtil.logout();
        return ResultUtil.success("退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    @Operation(operationId = "customer_get_current")
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
    @Operation(operationId = "customer_update_nickname")
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
    @Operation(operationId = "customer_update_gender")
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
    @Operation(operationId = "customer_update_birthdate")
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
    @Operation(operationId = "customer_update_preferences")
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
    @Operation(operationId = "customer_update_avatar")
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
    @Operation(operationId = "customer_get_merchants")
    public Result<Page<MerchantVO>> getMerchants(@RequestParam(defaultValue = "1") Integer pageNumber,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {

        // 参数校验，防止异常参数导致缓存击穿
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        // 构建缓存键
        String cacheKey = "merchants:page_" + pageNumber + "_" + pageSize;

        // 从Redisson获取RMapCache对象
        RMapCache<String, Result<Page<MerchantVO>>> cache = redisson.getMapCache("merchants_cache");

        // 使用Redisson的分布式锁防止缓存击穿
        RLock lock = redisson.getLock("merchants_lock:" + cacheKey);

        // 尝试从缓存获取结果
        Result<Page<MerchantVO>> result = cache.get(cacheKey);

        if (result != null) {
            return result;
        }

        try {
            // 使用尝试锁防止大量相同请求并发重建缓存(缓存击穿)
            // 等待2秒，持有锁10秒
            boolean lockAcquired = lock.tryLock(2, 10, TimeUnit.SECONDS);
            if (lockAcquired) {
                try {
                    // 双重检查，防止有其他线程已经重建了缓存
                    result = cache.get(cacheKey);
                    if (result != null) {
                        return result;
                    }

                    // 查询商户数据
                    Page<Merchant> merchantPage = merchantService.page(new Page<>(pageNumber, pageSize),
                            new LambdaQueryWrapper<>(Merchant.class).eq(Merchant::getStatus, StatusEnum.OPEN));

                    Page<MerchantVO> merchantVOPage = new Page<>(merchantPage.getCurrent(),
                            merchantPage.getSize(), merchantPage.getTotal());

                    merchantVOPage.setRecords(converter.convert(merchantPage.getRecords(), MerchantVO.class));

                    result = ResultUtil.success(merchantVOPage);

                    // 防止缓存穿透：即使是空结果也缓存，但时间较短
                    if (merchantVOPage.getRecords() == null || merchantVOPage.getRecords().isEmpty()) {
                        // 对于空结果，缓存时间较短，5分钟
                        cache.put(cacheKey, result, 5, TimeUnit.MINUTES);
                    } else {
                        // 正常结果使用随机过期时间，防止缓存雪崩
                        // 基础时间30分钟，随机增加0-10分钟的随机偏移
                        int randomExpiry = 30 + RandomUtil.randomInt(10);
                        cache.put(cacheKey, result, randomExpiry, TimeUnit.MINUTES);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                // 如果没有获取到锁，说明其他线程正在重建缓存，短暂等待后再查询
                Thread.sleep(200);
                // 递归调用，尝试再次获取
                return getMerchants(pageNumber, pageSize);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取商户列表缓存时发生中断异常", e);
            // 出现异常时，降级直接查询数据库
            return getDataFromDatabase(pageNumber, pageSize);
        } catch (Exception e) {
            log.error("获取商户列表缓存时发生异常", e);
            // 其他异常时，降级直接查询数据库
            return getDataFromDatabase(pageNumber, pageSize);
        }

        return result;
    }

    /**
     * 从数据库直接获取数据的备用方法(降级方法)
     * */
    private Result<Page<MerchantVO>> getDataFromDatabase(Integer pageNumber, Integer pageSize) {
        Page<Merchant> merchantPage = merchantService.page(new Page<>(pageNumber, pageSize),
                new LambdaQueryWrapper<>(Merchant.class).eq(Merchant::getStatus, StatusEnum.OPEN));

        Page<MerchantVO> merchantVOPage = new Page<>(merchantPage.getCurrent(),
                merchantPage.getSize(), merchantPage.getTotal());

        merchantVOPage.setRecords(converter.convert(merchantPage.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOPage);
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(operationId = "customer_get_merchant")
    public Result<MerchantVO> getMerchant(@PathVariable String merchantId) {

        Merchant merchant = merchantService.getById(merchantId);

        if (merchant == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "餐厅不存在");
        }

        MerchantVO merchantVO = converter.convert(merchant, MerchantVO.class);

        return ResultUtil.success(merchantVO);

    }

    @GetMapping("/menu/{merchantId}")
    @Operation(operationId = "customer_get_menu")
    public Result<List<Menu>> getMerchantMenus(@PathVariable String merchantId) {

        List<Menu> menuList = menuService.list(new LambdaQueryWrapper<>(Menu.class)
                .eq(Menu::getMerchantId, merchantId)
                .in(Menu::getStatus, MenuStatus.ENABLED, MenuStatus.SOLD_OUT));

        return ResultUtil.success(menuList);

    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/address/list")
    @Operation(operationId = "customer_address_list")
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
    @Operation(operationId = "customer_address_default")
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
    @Operation(operationId = "customer_address_add")
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
    @Operation(operationId = "customer_address_update")
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
    @Operation(operationId = "customer_address_set_default")
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
    @Operation(operationId = "customer_address_delete")
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