package club._8b1t.service.impl;

import club._8b1t.model.enums.merchant.StatusEnum;
import club._8b1t.service.CosService;
import club._8b1t.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Merchant;
import club._8b1t.service.MerchantService;
import club._8b1t.mapper.MerchantMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static club._8b1t.exception.ResultCode.*;

/**
* @author 8bit
* @description 针对表【merchant】的数据库操作Service实现
* @createDate 2025-03-07 21:08:39
*/
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
    implements MerchantService{

    @Resource
    private CosService cosService;

    @Override
    public boolean updateMerchantInfo(Merchant merchant) {
        return this.updateById(merchant);
    }

    @Override
    public boolean updateName(Long merchantId, String name) {
        Merchant merchant = this.getById(merchantId);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "店铺不存在");
        merchant.setName(name);
        return this.updateById(merchant);
    }

    @Override
    public boolean updateLogo(Long merchantId, @RequestParam("file") MultipartFile file) {
        Merchant merchant = this.getById(merchantId);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "店铺不存在");
        String logo = cosService.uploadLogo(merchantId, file);
        merchant.setLogo(logo);
        return this.updateById(merchant);
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
    public boolean updateStatus(Long merchantId, StatusEnum status) {
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<Merchant>()
                .eq(Merchant::getId, merchantId)
                .set(Merchant::getStatus, status);
        
        return this.update(updateWrapper);
    }
}




