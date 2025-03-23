package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.review.MerchantReviewCreateRequest;
import club._8b1t.model.dto.review.MerchantReviewQueryRequest;
import club._8b1t.model.dto.review.MerchantReviewUpdateRequest;
import club._8b1t.model.entity.MerchantReview;
import club._8b1t.model.entity.Order;
import club._8b1t.model.vo.MerchantReviewVO;
import club._8b1t.service.MerchantReviewService;
import club._8b1t.service.OrderService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant-review")
public class MerchantReviewController {

    @Resource
    private MerchantReviewService merchantReviewService;
    
    @Resource
    private OrderService orderService;
    
    @Resource
    private Converter converter;
    
    /**
     * 创建商家评价
     */
    @PostMapping("/create")
    public Result<Long> createReview(@RequestBody @Valid MerchantReviewCreateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证用户是否在该商家有完成的订单
        boolean hasCompletedOrder = orderService.hasCompletedOrderWithMerchant(userId, request.getMerchantId());
        if (!hasCompletedOrder) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您没有在该商家的已完成订单，无法评价");
        }
        
        // 检查是否已经评价过
        if (merchantReviewService.hasReviewed(userId, request.getMerchantId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经评价过此商家");
        }
        
        // 将请求转换为评价实体
        MerchantReview merchantReview = converter.convert(request, MerchantReview.class);
        merchantReview.setUserId(userId);
        
        // 创建评价
        Long reviewId = merchantReviewService.createReview(merchantReview);
        
        return ResultUtil.success("评价成功", reviewId);
    }
    
    /**
     * 获取商家评价列表（分页）
     */
    @PostMapping("/list")
    public Result<Page<MerchantReviewVO>> getReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody(required = false) MerchantReviewQueryRequest request) {
        
        // 创建查询条件
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getIsDeleted, 0);
        
        // 添加查询条件
        if (request != null) {
            // 按商家ID查询
            if (request.getMerchantId() != null) {
                queryWrapper.eq(MerchantReview::getMerchantId, request.getMerchantId());
            }
            
            // 按用户ID查询
            if (request.getUserId() != null) {
                queryWrapper.eq(MerchantReview::getUserId, request.getUserId());
            }
            
            // 按评分范围查询
            if (request.getMinRating() != null) {
                queryWrapper.ge(MerchantReview::getRating, request.getMinRating());
            }
            
            if (request.getMaxRating() != null) {
                queryWrapper.le(MerchantReview::getRating, request.getMaxRating());
            }
            
            // 是否只查询匿名评价
            if (request.getIsAnonymous() != null) {
                queryWrapper.eq(MerchantReview::getIsAnonymous, request.getIsAnonymous());
            }
        }
        
        // 默认按创建时间倒序排序
        queryWrapper.orderByDesc(MerchantReview::getCreatedAt);
        
        // 执行分页查询
        Page<MerchantReview> page = merchantReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MerchantReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MerchantReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取特定商家的评价列表
     */
    @GetMapping("/merchant/{merchantId}")
    public Result<Page<MerchantReviewVO>> getMerchantReviews(
            @PathVariable Integer merchantId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 创建查询条件
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getMerchantId, merchantId)
                   .eq(MerchantReview::getIsDeleted, 0)
                   .orderByDesc(MerchantReview::getCreatedAt);
        
        // 执行分页查询
        Page<MerchantReview> page = merchantReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MerchantReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MerchantReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取用户的评价列表
     */
    @GetMapping("/user")
    public Result<Page<MerchantReviewVO>> getUserReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 创建查询条件
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantReview::getUserId, userId)
                   .eq(MerchantReview::getIsDeleted, 0)
                   .orderByDesc(MerchantReview::getCreatedAt);
        
        // 执行分页查询
        Page<MerchantReview> page = merchantReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MerchantReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MerchantReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    public Result<MerchantReviewVO> getReview(@PathVariable Long id) {
        // 获取评价信息
        MerchantReview review = merchantReviewService.getById(id);
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 转换为VO
        MerchantReviewVO reviewVO = converter.convert(review, MerchantReviewVO.class);
        
        return ResultUtil.success(reviewVO);
    }
    
    /**
     * 更新评价信息
     */
    @PostMapping("/update")
    public Result<String> updateReview(@RequestBody @Valid MerchantReviewUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 获取评价并验证所有权
        MerchantReview review = merchantReviewService.getById(request.getReviewId());
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 验证是否是评价的创建者
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权更新此评价");
        }
        
        // 将请求转换为评价实体
        MerchantReview updatedReview = converter.convert(request, MerchantReview.class);
        updatedReview.setUserId(userId);
        
        // 更新评价
        boolean updated = merchantReviewService.updateReview(updatedReview);
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("更新成功");
    }
    
    /**
     * 删除评价
     */
    @GetMapping("/{id}/delete")
    public Result<String> deleteReview(@PathVariable Long id) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 获取评价并验证所有权
        MerchantReview review = merchantReviewService.getById(id);
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 验证是否是评价的创建者
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权删除此评价");
        }
        
        // 删除评价（软删除）
        boolean deleted = merchantReviewService.removeById(id);
        if (!deleted) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        
        return ResultUtil.success("删除成功");
    }
} 