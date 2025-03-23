package club._8b1t.service;

import club._8b1t.model.entity.CustomerAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author root
* @description 针对表【customer_address(用户外卖地址表)】的数据库操作Service
* @createDate 2025-03-21 13:51:00
*/
public interface CustomerAddressService extends IService<CustomerAddress> {

    /**
     * 设置地址为默认地址
     * @param addressId 地址ID
     * @param customerId 用户ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Long addressId, Long customerId);

    /**
     * 获取用户的默认地址
     * @param customerId 用户ID
     * @return 默认地址
     */
    CustomerAddress getDefaultAddress(Long customerId);

    /**
     * 根据用户ID获取所有地址列表
     * @param customerId 用户ID
     * @return 地址列表
     */
    List<CustomerAddress> getAddressList(Long customerId);
    
    /**
     * 添加新地址
     * @param address 地址信息
     * @return 是否添加成功
     */
    boolean addAddress(CustomerAddress address);
    
    /**
     * 更新地址信息
     * @param address 地址信息
     * @return 是否更新成功
     */
    boolean updateAddress(CustomerAddress address);
}
