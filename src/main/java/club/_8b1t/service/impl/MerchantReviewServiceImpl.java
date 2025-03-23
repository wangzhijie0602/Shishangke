package club._8b1t.service.impl;

import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.entity.MerchantReview;
import club._8b1t.mapper.MerchantReviewMapper;
import club._8b1t.service.MerchantReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 商家评价服务实现类
 */
@Service
public class MerchantReviewServiceImpl extends ServiceImpl<MerchantReviewMapper, MerchantReview> implements MerchantReviewService {
    
    /**
     * 创建商家评价
     *
     * @param merchantReview 商家评价信息
     * @return 评价ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createReview(MerchantReview merchantReview) {
        // 处理图片列表
        if (merchantReview.getImages() == null || merchantReview.getImages().isEmpty()) {
            merchantReview.setImages(null);
        }
        
        // 保存评价
        boolean saved = save(merchantReview);
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建评价失败");
        }
        
        return merchantReview.getReviewId();
    }
    
    /**
     * 更新商家评价
     *
     * @param merchantReview 商家评价信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReview(MerchantReview merchantReview) {
        // 获取原评价信息
        MerchantReview existingReview = getById(merchantReview.getReviewId());
        if (existingReview == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 更新评价信息
        return updateById(merchantReview);
    }
    
    /**
     * 检查用户是否已经评价过某个商家
     *
     * @param userId     用户ID
     * @param merchantId 商家ID
     * @return 是否已评价
     */
    @Override
    public boolean hasReviewed(Long userId, Integer merchantId) {
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getUserId, userId)
                   .eq(MerchantReview::getMerchantId, merchantId)
                   .eq(MerchantReview::getIsDeleted, 0);
                   
        return count(queryWrapper) > 0;
    }
    
    /**
     * 获取商家的平均评分
     *
     * @param merchantId 商家ID
     * @return 平均评分
     */
    @Override
    public double getAverageRating(Integer merchantId) {
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getMerchantId, merchantId)
                   .eq(MerchantReview::getIsDeleted, 0)
                   .select(MerchantReview::getRating);
                   
        List<MerchantReview> reviews = list(queryWrapper);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0.0;
        for (MerchantReview review : reviews) {
            totalRating += review.getRating().doubleValue();
        }
        
        // 计算平均分并保留一位小数
        BigDecimal avgRating = new BigDecimal(totalRating / reviews.size())
                .setScale(1, RoundingMode.HALF_UP);
                
        return avgRating.doubleValue();
    }
    
    /**
     * 获取商家的评价数量
     *
     * @param merchantId 商家ID
     * @return 评价数量
     */
    @Override
    public int getReviewCount(Integer merchantId) {
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getMerchantId, merchantId)
                   .eq(MerchantReview::getIsDeleted, 0);
                   
        return Math.toIntExact(count(queryWrapper));
    }
}




