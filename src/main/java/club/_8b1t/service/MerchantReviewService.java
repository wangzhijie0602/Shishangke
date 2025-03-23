package club._8b1t.service;

import club._8b1t.model.entity.MerchantReview;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商家评价服务接口
 */
public interface MerchantReviewService extends IService<MerchantReview> {
    
    /**
     * 创建商家评价
     *
     * @param merchantReview 商家评价信息
     * @return 评价ID
     */
    Long createReview(MerchantReview merchantReview);
    
    /**
     * 更新商家评价
     *
     * @param merchantReview 商家评价信息
     * @return 是否成功
     */
    boolean updateReview(MerchantReview merchantReview);
    
    /**
     * 检查用户是否已经评价过某个商家
     *
     * @param userId     用户ID
     * @param merchantId 商家ID
     * @return 是否已评价
     */
    boolean hasReviewed(Long userId, Integer merchantId);
    
    /**
     * 获取商家的平均评分
     *
     * @param merchantId 商家ID
     * @return 平均评分
     */
    double getAverageRating(Integer merchantId);
    
    /**
     * 获取商家的评价数量
     *
     * @param merchantId 商家ID
     * @return 评价数量
     */
    int getReviewCount(Integer merchantId);
}
