package club._8b1t.service;

import club._8b1t.model.entity.MenuReview;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 菜品评价服务接口
 */
public interface MenuReviewService extends IService<MenuReview> {
    
    /**
     * 创建菜品评价
     *
     * @param menuReview 菜品评价信息
     * @return 评价ID
     */
    Long createReview(MenuReview menuReview);
    
    /**
     * 更新菜品评价
     *
     * @param menuReview 菜品评价信息
     * @return 是否成功
     */
    boolean updateReview(MenuReview menuReview);
    
    /**
     * 检查用户是否已经评价过某个菜品
     *
     * @param userId  用户ID
     * @param menuId  菜品ID
     * @param orderId 订单ID
     * @return 是否已评价
     */
    boolean hasReviewed(Long userId, Integer menuId, Long orderId);
    
    /**
     * 获取菜品的平均评分
     *
     * @param menuId 菜品ID
     * @return 平均评分
     */
    double getAverageRating(Integer menuId);
    
    /**
     * 获取菜品的评价数量
     *
     * @param menuId 菜品ID
     * @return 评价数量
     */
    int getReviewCount(Integer menuId);
    
    /**
     * 给评价点赞
     *
     * @param reviewId 评价ID
     * @return 点赞后的数量
     */
    int likeReview(Long reviewId);
}
