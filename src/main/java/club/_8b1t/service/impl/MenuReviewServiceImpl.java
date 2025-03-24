package club._8b1t.service.impl;

import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.entity.MenuReview;
import club._8b1t.mapper.MenuReviewMapper;
import club._8b1t.service.MenuReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 菜品评价服务实现类
 */
@Service
public class MenuReviewServiceImpl extends ServiceImpl<MenuReviewMapper, MenuReview> implements MenuReviewService {
    
    /**
     * 创建菜品评价
     *
     * @param menuReview 菜品评价信息
     * @return 评价ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createReview(MenuReview menuReview) {
        // 初始化点赞数量
        if (menuReview.getLikesCount() == null) {
            menuReview.setLikesCount(0);
        }
        
        // 处理图片列表
        if (menuReview.getImages() == null || menuReview.getImages().isEmpty()) {
            menuReview.setImages(null);
        }
        
        // 保存评价
        boolean saved = save(menuReview);
        if (!saved) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "创建评价失败");
        }
        
        return menuReview.getReviewId();
    }
    
    /**
     * 更新菜品评价
     *
     * @param menuReview 菜品评价信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReview(MenuReview menuReview) {
        // 获取原评价信息
        MenuReview existingReview = getById(menuReview.getReviewId());
        if (existingReview == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评价不存在");
        }
        
        // 保留原点赞数量
        menuReview.setLikesCount(existingReview.getLikesCount());
        
        // 更新评价信息
        return updateById(menuReview);
    }
    
    /**
     * 检查用户是否已经评价过某个菜品
     *
     * @param userId  用户ID
     * @param menuId  菜品ID
     * @param orderId 订单ID
     * @return 是否已评价
     */
    @Override
    public boolean hasReviewed(Long userId, Integer menuId, Long orderId) {
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getUserId, userId)
                   .eq(MenuReview::getMenuId, menuId)
                   .eq(MenuReview::getOrderId, orderId)
                   .eq(MenuReview::getIsDeleted, 0);
                   
        return count(queryWrapper) > 0;
    }
    
    /**
     * 获取菜品的平均评分
     *
     * @param menuId 菜品ID
     * @return 平均评分
     */
    @Override
    public double getAverageRating(Integer menuId) {
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getMenuId, menuId)
                   .eq(MenuReview::getIsDeleted, 0)
                   .select(MenuReview::getRating);
                   
        List<MenuReview> reviews = list(queryWrapper);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0.0;
        for (MenuReview review : reviews) {
            totalRating += review.getRating().doubleValue();
        }
        
        // 计算平均分并保留一位小数
        BigDecimal avgRating = new BigDecimal(totalRating / reviews.size())
                .setScale(1, RoundingMode.HALF_UP);
                
        return avgRating.doubleValue();
    }
    
    /**
     * 获取菜品的评价数量
     *
     * @param menuId 菜品ID
     * @return 评价数量
     */
    @Override
    public int getReviewCount(Integer menuId) {
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getMenuId, menuId)
                   .eq(MenuReview::getIsDeleted, 0);
                   
        return Math.toIntExact(count(queryWrapper));
    }
    
    /**
     * 给评价点赞
     *
     * @param reviewId 评价ID
     * @return 点赞后的数量
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int likeReview(Long reviewId) {
        MenuReview review = getById(reviewId);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评价不存在");
        }
        
        // 增加点赞数量
        int newLikesCount = review.getLikesCount() + 1;
        
        LambdaUpdateWrapper<MenuReview> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MenuReview::getReviewId, reviewId)
                    .set(MenuReview::getLikesCount, newLikesCount);
                    
        boolean updated = update(updateWrapper);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "点赞失败");
        }
        
        return newLikesCount;
    }
}




