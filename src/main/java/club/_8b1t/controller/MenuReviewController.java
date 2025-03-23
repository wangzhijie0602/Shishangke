package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.review.MenuReviewCreateRequest;
import club._8b1t.model.dto.review.MenuReviewQueryRequest;
import club._8b1t.model.dto.review.MenuReviewUpdateRequest;
import club._8b1t.model.entity.MenuReview;
import club._8b1t.model.entity.Order;
import club._8b1t.model.vo.MenuReviewVO;
import club._8b1t.service.MenuReviewService;
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
@RequestMapping("/api/v1/menu-review")
public class MenuReviewController {

    @Resource
    private MenuReviewService menuReviewService;
    
    @Resource
    private OrderService orderService;
    
    @Resource
    private Converter converter;
    
    /**
     * 创建菜品评价
     */
    @PostMapping("/create")
    public Result<Long> createReview(@RequestBody @Valid MenuReviewCreateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证订单是否属于当前用户且状态为已完成
        Order order = orderService.getOrderByIdAndUserId(request.getOrderId(), userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权操作");
        }
        
        // 验证订单状态是否为已完成
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单未完成，无法评价");
        }
        
        // 检查是否已经评价过
        if (menuReviewService.hasReviewed(userId, request.getMenuId(), order.getOrderId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经评价过此菜品");
        }
        
        // 将请求转换为评价实体
        MenuReview menuReview = converter.convert(request, MenuReview.class);
        menuReview.setUserId(userId);
        
        // 创建评价
        Long reviewId = menuReviewService.createReview(menuReview);
        
        return ResultUtil.success("评价成功", reviewId);
    }
    
    /**
     * 获取菜品评价列表（分页）
     */
    @PostMapping("/list")
    public Result<Page<MenuReviewVO>> getReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody(required = false) MenuReviewQueryRequest request) {
        
        // 创建查询条件
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getIsDeleted, 0);
        
        // 添加查询条件
        if (request != null) {
            // 按菜品ID查询
            if (request.getMenuId() != null) {
                queryWrapper.eq(MenuReview::getMenuId, request.getMenuId());
            }
            
            // 按用户ID查询
            if (request.getUserId() != null) {
                queryWrapper.eq(MenuReview::getUserId, request.getUserId());
            }
            
            // 按评分范围查询
            if (request.getMinRating() != null) {
                queryWrapper.ge(MenuReview::getRating, request.getMinRating());
            }
            
            if (request.getMaxRating() != null) {
                queryWrapper.le(MenuReview::getRating, request.getMaxRating());
            }
        }
        
        // 默认按创建时间倒序排序
        queryWrapper.orderByDesc(MenuReview::getCreatedAt);
        
        // 执行分页查询
        Page<MenuReview> page = menuReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MenuReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MenuReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取特定菜品的评价列表
     */
    @GetMapping("/menu/{menuId}")
    public Result<Page<MenuReviewVO>> getMenuReviews(
            @PathVariable Long menuId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 创建查询条件
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getMenuId, menuId)
                   .eq(MenuReview::getIsDeleted, 0)
                   .orderByDesc(MenuReview::getCreatedAt);
        
        // 执行分页查询
        Page<MenuReview> page = menuReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MenuReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MenuReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取用户的评价列表
     */
    @GetMapping("/user")
    public Result<Page<MenuReviewVO>> getUserReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 创建查询条件
        LambdaQueryWrapper<MenuReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuReview::getUserId, userId)
                   .eq(MenuReview::getIsDeleted, 0)
                   .orderByDesc(MenuReview::getCreatedAt);
        
        // 执行分页查询
        Page<MenuReview> page = menuReviewService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<MenuReviewVO> reviewVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        reviewVOPage.setRecords(converter.convert(page.getRecords(), MenuReviewVO.class));
        
        return ResultUtil.success(reviewVOPage);
    }
    
    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    public Result<MenuReviewVO> getReview(@PathVariable Long id) {
        // 获取评价信息
        MenuReview review = menuReviewService.getById(id);
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 转换为VO
        MenuReviewVO reviewVO = converter.convert(review, MenuReviewVO.class);
        
        return ResultUtil.success(reviewVO);
    }
    
    /**
     * 更新评价信息
     */
    @PostMapping("/update")
    public Result<String> updateReview(@RequestBody @Valid MenuReviewUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 获取评价并验证所有权
        MenuReview review = menuReviewService.getById(request.getReviewId());
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 验证是否是评价的创建者
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权更新此评价");
        }
        
        // 将请求转换为评价实体
        MenuReview updatedReview = converter.convert(request, MenuReview.class);
        updatedReview.setUserId(userId);
        
        // 更新评价
        boolean updated = menuReviewService.updateReview(updatedReview);
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
        MenuReview review = menuReviewService.getById(id);
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 验证是否是评价的创建者
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权删除此评价");
        }
        
        // 删除评价（软删除）
        boolean deleted = menuReviewService.removeById(id);
        if (!deleted) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        
        return ResultUtil.success("删除成功");
    }
    
    /**
     * 点赞评价
     */
    @GetMapping("/{id}/like")
    public Result<Integer> likeReview(@PathVariable Long id) {
        // 获取评价信息
        MenuReview review = menuReviewService.getById(id);
        if (review == null || review.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评价不存在");
        }
        
        // 点赞评价
        int likesCount = menuReviewService.likeReview(id);
        
        return ResultUtil.success("点赞成功", likesCount);
    }
} 