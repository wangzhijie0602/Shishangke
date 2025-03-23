package club._8b1t.service.impl;

import club._8b1t.model.entity.User;
import club._8b1t.service.CosService;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Customer;
import club._8b1t.service.CustomerService;
import club._8b1t.mapper.CustomerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.charset.StandardCharsets;

/**
 * @author root
 * @description 针对表【customer(顾客信息表)】的数据库操作Service实现
 * @createDate 2025-03-16 16:07:17
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>
        implements CustomerService {

    @Resource
    private CosService cosService;

    @Override
    public Customer getCustomerByUsernameAndPassword(String username, String password) {

        Customer customer = getCustomerByUsername(username);

        if (customer == null || !BCrypt.checkpw(password, customer.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        return customer;

    }

    /**
     * 通过用户名获取顾客信息
     *
     * @param username 用户名
     * @return 顾客信息
     */
    @Override
    public Customer getCustomerByUsername(String username) {

        if (StrUtil.isBlank(username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能为空");
        }

        return this.getOne(new LambdaQueryWrapper<>(Customer.class)
                .eq(Customer::getUsername, username));
    }

    /**
     * 创建顾客信息
     *
     * @param customer 顾客信息
     * @return 顾客ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createCustomer(Customer customer) {
        // 检查用户名是否已存在
        Customer existingCustomer = getCustomerByUsername(customer.getUsername());
        if (existingCustomer != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 初始化VIP等级和积分
        if (customer.getVipLevel() == null) {
            customer.setVipLevel(0);
        }

        if (customer.getPoints() == null) {
            customer.setPoints(0);
        }

        if (customer.getNickname() == null) {
            customer.setNickname("用户" + RandomUtil.randomString(8));
        }

        // 如果提供了出生日期字符串，则转换为LocalDate
        String birthDateStr = customer.getBirthDate() != null ? customer.getBirthDate().toString() : null;
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
                customer.setBirthDate(LocalDate.parse(birthDateStr));
            } catch (DateTimeParseException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "出生日期格式无效");
            }
        }

        // 对密码进行加密
        if (customer.getPassword() != null) {
            String encryptedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt());
            customer.setPassword(encryptedPassword);
        }

        // 保存顾客信息
        boolean saved = save(customer);
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建顾客信息失败");
        }

        return customer.getId();
    }

    /**
     * 更新顾客信息
     *
     * @param customer 顾客信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCustomerInfo(Customer customer) {
        // 检查顾客是否存在
        Customer existingCustomer = getById(customer.getId());
        if (existingCustomer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客信息不存在");
        }

        // 如果更新了密码，需要加密
        if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
            String encryptedPassword = DigestUtils.md5DigestAsHex(customer.getPassword().getBytes(StandardCharsets.UTF_8));
            customer.setPassword(encryptedPassword);
        }

        // 清空avatar字段，头像更新通过专门的方法处理
        customer.setAvatar(null);

        // 更新顾客信息
        return updateById(customer);
    }

    /**
     * 更新顾客真实姓名
     *
     * @param customerId 顾客ID
     * @param nickname   真实姓名
     * @return 是否成功
     */
    @Override
    public boolean updateNickname(Long customerId, String nickname) {
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getNickname, nickname);

        return update(updateWrapper);
    }

    /**
     * 更新顾客性别
     *
     * @param customerId 顾客ID
     * @param gender     性别
     * @return 是否成功
     */
    @Override
    public boolean updateGender(Long customerId, String gender) {
        // 验证性别值
        if (!isValidGender(gender)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的性别值");
        }

        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getGender, gender);

        return update(updateWrapper);
    }

    /**
     * 更新顾客出生日期
     *
     * @param customerId 顾客ID
     * @param birthDate  出生日期字符串
     * @return 是否成功
     */
    @Override
    public boolean updateBirthDate(Long customerId, String birthDate) {
        // 转换出生日期字符串为LocalDate
        LocalDate birthDateObj;
        try {
            birthDateObj = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "出生日期格式无效");
        }

        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getBirthDate, birthDateObj);

        return update(updateWrapper);
    }

    /**
     * 更新顾客饮食偏好
     *
     * @param customerId  顾客ID
     * @param preferences 饮食偏好
     * @return 是否成功
     */
    @Override
    public boolean updatePreferences(Long customerId, String preferences) {
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getPreferences, preferences);

        return update(updateWrapper);
    }

    /**
     * 上传并更新顾客头像
     *
     * @param customerId 顾客ID
     * @param file       头像文件
     * @return 头像URL，失败返回null
     */
    @Override
    public String updateAvatar(Long customerId, MultipartFile file) {
        // 验证用户是否存在
        if (this.getById(customerId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 上传头像
        String avatarUrl = cosService.uploadAvatar(customerId, file);

        // 更新用户头像
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<Customer>()
                .eq(Customer::getId, customerId)
                .set(Customer::getAvatar, avatarUrl);

        boolean success = this.update(updateWrapper);

        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新头像失败");
        }

        return avatarUrl;
    }

    /**
     * 删除顾客信息
     *
     * @param customerId 顾客ID
     * @return 是否成功
     */
    @Override
    public boolean deleteCustomer(Long customerId) {
        return removeById(customerId);
    }

    /**
     * 增加积分
     *
     * @param customerId 顾客ID
     * @param points     增加的积分
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addPoints(Long customerId, Integer points) {
        if (points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "增加的积分必须大于0");
        }

        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客信息不存在");
        }

        int newPoints = customer.getPoints() + points;

        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getPoints, newPoints);

        return update(updateWrapper);
    }

    /**
     * 使用积分
     *
     * @param customerId 顾客ID
     * @param points     使用的积分
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean usePoints(Long customerId, Integer points) {
        if (points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用的积分必须大于0");
        }

        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客信息不存在");
        }

        // 检查积分是否足够
        if (customer.getPoints() < points) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }

        int newPoints = customer.getPoints() - points;

        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getPoints, newPoints);

        return update(updateWrapper);
    }

    /**
     * 升级VIP等级
     *
     * @param customerId 顾客ID
     * @param vipLevel   要升级到的VIP等级
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean upgradeVipLevel(Long customerId, Integer vipLevel) {
        if (vipLevel < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "VIP等级必须大于等于0");
        }

        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客信息不存在");
        }

        // 检查是否为升级（不允许降级）
        if (customer.getVipLevel() > vipLevel) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能降级VIP等级");
        }

        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customerId)
                .set(Customer::getVipLevel, vipLevel);

        return update(updateWrapper);
    }

    /**
     * 验证性别值是否有效
     *
     * @param gender 性别值
     * @return 是否有效
     */
    private boolean isValidGender(String gender) {
        return "MALE".equals(gender) || "FEMALE".equals(gender) || "OTHER".equals(gender);
    }
}




