package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.order.OrderCreateRequest;
import club._8b1t.model.dto.order.OrderQueryRequest;
import club._8b1t.model.dto.order.OrderUpdateRequest;
import club._8b1t.model.entity.Order;
import club._8b1t.model.vo.OrderVO;
import club._8b1t.service.OrderService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Resource
    private OrderService orderService;
    
    @Resource
    private Converter converter;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 将请求转换为订单实体
        Order order = converter.convert(request, Order.class);
        order.setUserId(userId);
        
        // 创建订单
        Long orderId = orderService.createOrder(order);
        
        return ResultUtil.success("订单创建成功", orderId);
    }
    
    /**
     * 获取订单列表
     */
    @PostMapping("/list")
    public Result<Page<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody(required = false) OrderQueryRequest request) {
        
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 创建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                   .eq(Order::getIsDeleted, 0);
        
        // 添加查询条件
        if (request != null) {
            // 按订单状态筛选
            if (request.getStatus() != null) {
                queryWrapper.eq(Order::getStatus, request.getStatus());
            }
            
            // 按商家ID筛选
            if (request.getMerchantId() != null) {
                queryWrapper.eq(Order::getMerchantId, request.getMerchantId());
            }
            
            // 按订单创建时间排序
            queryWrapper.orderByDesc(Order::getCreatedAt);
        }
        
        // 执行分页查询
        Page<Order> page = orderService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO对象
        Page<OrderVO> orderVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        orderVOPage.setRecords(converter.convert(page.getRecords(), OrderVO.class));
        
        return ResultUtil.success(orderVOPage);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 获取订单并验证所有权
        Order order = orderService.getOrderByIdAndUserId(id, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权查看");
        }
        
        // 转换为VO
        OrderVO orderVO = converter.convert(order, OrderVO.class);
        
        return ResultUtil.success(orderVO);
    }
    
    /**
     * 更新订单状态
     */
    @PostMapping("/{id}/update-status")
    public Result<String> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证订单所有权
        Order order = orderService.getOrderByIdAndUserId(id, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权操作");
        }
        
        // 更新订单状态
        boolean updated = orderService.updateOrderStatus(id, status);
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("状态更新成功");
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id, @RequestParam(required = false) String reason) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证订单所有权
        Order order = orderService.getOrderByIdAndUserId(id, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权操作");
        }
        
        // 取消订单
        boolean cancelled = orderService.cancelOrder(id, reason);
        if (!cancelled) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消失败");
        }
        
        return ResultUtil.success("订单已取消");
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public Result<String> payOrder(@PathVariable Long id, @RequestParam String paymentMethod) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证订单所有权
        Order order = orderService.getOrderByIdAndUserId(id, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权操作");
        }
        
        // 支付订单
        boolean paid = orderService.payOrder(id, paymentMethod);
        if (!paid) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付失败");
        }
        
        return ResultUtil.success("支付成功");
    }
    
    /**
     * 删除订单（软删除）
     */
    @GetMapping("/{id}/delete")
    public Result<String> deleteOrder(@PathVariable Long id) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证订单所有权
        Order order = orderService.getOrderByIdAndUserId(id, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在或无权操作");
        }
        
        // 删除订单（软删除）
        boolean deleted = orderService.deleteOrder(id);
        if (!deleted) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        
        return ResultUtil.success("删除成功");
    }
} 