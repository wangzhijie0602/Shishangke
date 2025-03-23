package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.CustomerAddress;
import club._8b1t.service.CustomerAddressService;
import club._8b1t.mapper.CustomerAddressMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author root
* @description 针对表【customer_address(用户外卖地址表)】的数据库操作Service实现
* @createDate 2025-03-21 13:51:00
*/
@Service
public class CustomerAddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress>
    implements CustomerAddressService{
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long addressId, Long customerId) {
        // 首先将所有地址设为非默认
        LambdaUpdateWrapper<CustomerAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CustomerAddress::getCustomerId, customerId)
                     .set(CustomerAddress::getIsDefault, 0);
        update(updateWrapper);
        
        // 将指定地址设为默认
        updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CustomerAddress::getId, addressId)
                     .eq(CustomerAddress::getCustomerId, customerId);
        return update(updateWrapper);
    }

    @Override
    public CustomerAddress getDefaultAddress(Long customerId) {
        LambdaQueryWrapper<CustomerAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerAddress::getCustomerId, customerId)
                    .eq(CustomerAddress::getIsDefault, 1)
                    .eq(CustomerAddress::getIsDeleted, 0);
        return getOne(queryWrapper);
    }

    @Override
    public List<CustomerAddress> getAddressList(Long customerId) {
        LambdaQueryWrapper<CustomerAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerAddress::getCustomerId, customerId)
                    .eq(CustomerAddress::getIsDeleted, 0)
                    .orderByDesc(CustomerAddress::getIsDefault)
                    .orderByDesc(CustomerAddress::getUpdatedAt);
        return list(queryWrapper);
    }

    @Override
    public boolean addAddress(CustomerAddress address) {
        // 如果添加的是默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaUpdateWrapper<CustomerAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CustomerAddress::getCustomerId, address.getCustomerId())
                         .set(CustomerAddress::getIsDefault, 0);
            update(updateWrapper);
        }
        return save(address);
    }

    @Override
    public boolean updateAddress(CustomerAddress address) {
        // 如果更新为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaUpdateWrapper<CustomerAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CustomerAddress::getCustomerId, address.getCustomerId())
                         .set(CustomerAddress::getIsDefault, 0);
            update(updateWrapper);
        }
        return updateById(address);
    }
}




