package club._8b1t.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Merchant;
import club._8b1t.service.MerchantService;
import club._8b1t.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
* @author 8bit
* @description 针对表【merchant】的数据库操作Service实现
* @createDate 2025-03-07 21:08:39
*/
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
    implements MerchantService{

    @Override
    public Merchant getMerchantByIdAndUserId(Long merchantId, Long userId) {
        // 查询指定ID的商家是否属于当前用户
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .eq(Merchant::getUserId, userId);
        
        return this.getOne(queryWrapper);
    }

    @Override
    public Page<Merchant> getMerchantPage(Integer pageNumber, Integer pageSize) {
        Page<Merchant> page = new Page<>(pageNumber, pageSize);
        return this.page(page);
    }

    @Override
    public boolean updateMerchantInfo(Merchant merchant) {
        return this.updateById(merchant);
    }

    @Override
    public boolean updateName(Long merchantId, String name) {
        // 创建更新条件
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getName, name);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateLogo(Long merchantId, String logo) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getLogo, logo);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updatePhone(Long merchantId, String phone) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getPhone, phone);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateAddress(Long merchantId, String province, String city, String district, 
                               String street, String addressDetail) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getProvince, province)
                .set(Merchant::getCity, city)
                .set(Merchant::getDistrict, district)
                .set(Merchant::getStreet, street)
                .set(Merchant::getAddressDetail, addressDetail);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateBusinessHours(Long merchantId, String openTime, String closeTime) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getOpenTime, openTime)
                .set(Merchant::getCloseTime, closeTime);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateDescription(Long merchantId, String description) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getDescription, description);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateMinPrice(Long merchantId, BigDecimal minPrice) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getMinPrice, minPrice);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateStatus(Long merchantId, String status) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getStatus, status);
        
        return this.update(updateWrapper);
    }
}




