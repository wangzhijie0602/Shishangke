package club._8b1t.service;

import club._8b1t.model.entity.Merchant;
import club._8b1t.model.enums.MerchantStatusEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 8bit
* @description 针对表【merchant】的数据库操作Service接口
* @createDate 2025-03-07 21:08:39
*/
public interface MerchantService extends IService<Merchant> {

    Page<Merchant> getMerchantPage(Integer pageNumber, Integer pageSize);
    
    /**
     * 更新商家基本信息
     * 
     * @param merchant 要更新的商家对象
     * @return 是否更新成功
     */
    boolean updateMerchantInfo(Merchant merchant);
    
    /**
     * 更新商家名称
     * 
     * @param merchantId 商家ID
     * @param name 新的商家名称
     * @return 是否更新成功
     */
    boolean updateName(Long merchantId, String name);
    
    /**
     * 更新商家Logo
     * 
     * @param merchantId 商家ID
     * @param logo 新的Logo URL
     * @return 是否更新成功
     */
    boolean updateLogo(Long merchantId, String logo);
    
    /**
     * 更新商家电话
     * 
     * @param merchantId 商家ID
     * @param phone 新的电话号码
     * @return 是否更新成功
     */
    boolean updatePhone(Long merchantId, String phone);
    
    /**
     * 更新商家地址
     * 
     * @param merchantId 商家ID
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param street 街道
     * @param addressDetail 详细地址
     * @return 是否更新成功
     */
    boolean updateAddress(Long merchantId, String province, String city, String district, 
                         String street, String addressDetail);
    
    /**
     * 更新商家营业时间
     * 
     * @param merchantId 商家ID
     * @param openTime 开店时间
     * @param closeTime 关店时间
     * @return 是否更新成功
     */
    boolean updateBusinessHours(Long merchantId, String openTime, String closeTime);
    
    /**
     * 更新商家描述
     * 
     * @param merchantId 商家ID
     * @param description 新的描述信息
     * @return 是否更新成功
     */
    boolean updateDescription(Long merchantId, String description);
    
    /**
     * 更新商家最低起送价
     * 
     * @param merchantId 商家ID
     * @param minPrice 新的最低起送价
     * @return 是否更新成功
     */
    boolean updateMinPrice(Long merchantId, java.math.BigDecimal minPrice);
    
    /**
     * 更新商家状态
     * 
     * @param merchantId 商家ID
     * @param status 新的状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long merchantId, MerchantStatusEnum status);
}
