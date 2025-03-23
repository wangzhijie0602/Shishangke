package club._8b1t.service;

import club._8b1t.model.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 顾客服务接口
 */
public interface CustomerService extends IService<Customer> {

    Customer getCustomerByUsernameAndPassword(String username, String password);
    
    /**
     * 创建顾客信息
     *
     * @param customer 顾客信息
     * @return 顾客ID
     */
    Long createCustomer(Customer customer);
    
    /**
     * 通过用户名获取顾客信息
     *
     * @param username 用户名
     * @return 顾客信息
     */
    Customer getCustomerByUsername(String username);
    
    /**
     * 更新顾客信息
     *
     * @param customer 顾客信息
     * @return 是否成功
     */
    boolean updateCustomerInfo(Customer customer);

    /**
     * 更新顾客真实姓名
     *
     * @param customerId 顾客ID
     * @param nickname 真实姓名
     * @return 是否成功
     */
    boolean updateNickname(Long customerId, String nickname);
    
    /**
     * 更新顾客性别
     *
     * @param customerId 顾客ID
     * @param gender 性别
     * @return 是否成功
     */
    boolean updateGender(Long customerId, String gender);
    
    /**
     * 更新顾客出生日期
     *
     * @param customerId 顾客ID
     * @param birthDate 出生日期
     * @return 是否成功
     */
    boolean updateBirthDate(Long customerId, String birthDate);
    
    /**
     * 更新顾客饮食偏好
     *
     * @param customerId 顾客ID
     * @param preferences 饮食偏好
     * @return 是否成功
     */
    boolean updatePreferences(Long customerId, String preferences);
    
    /**
     * 上传并更新顾客头像
     *
     * @param customerId 顾客ID
     * @param file 头像文件
     * @return 头像URL，失败返回null
     */
    String updateAvatar(Long customerId, MultipartFile file);
    
    /**
     * 删除顾客信息
     *
     * @param customerId 顾客ID
     * @return 是否成功
     */
    boolean deleteCustomer(Long customerId);
    
    /**
     * 增加积分
     *
     * @param customerId 顾客ID
     * @param points 增加的积分
     * @return 是否成功
     */
    boolean addPoints(Long customerId, Integer points);
    
    /**
     * 使用积分
     *
     * @param customerId 顾客ID
     * @param points 使用的积分
     * @return 是否成功
     */
    boolean usePoints(Long customerId, Integer points);
    
    /**
     * 升级VIP等级
     *
     * @param customerId 顾客ID
     * @param vipLevel 要升级到的VIP等级
     * @return 是否成功
     */
    boolean upgradeVipLevel(Long customerId, Integer vipLevel);
}
