package club._8b1t.service.impl;

import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.entity.Order;
import club._8b1t.mapper.OrderMapper;
import club._8b1t.service.OrderService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createOrder(Order order) {
        // 生成唯一订单号
        order.setOrderNumber(generateOrderNumber());
        
        // 设置初始状态
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        
        // 保存订单
        boolean saved = save(order);
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
        
        return order.getOrderId();
    }
    
    /**
     * 获取订单详情
     *
     * @param id     订单ID
     * @param userId 用户ID
     * @return 订单信息
     */
    @Override
    public Order getOrderByIdAndUserId(Long id, Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderId, id)
                   .eq(Order::getUserId, userId)
                   .eq(Order::getIsDeleted, 0);
                   
        return getOne(queryWrapper);
    }
    
    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderStatus(Long id, String status) {
        // 验证状态值
        if (!isValidStatus(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的订单状态");
        }
        
        Order order = getById(id);
        if (order == null) {
            return false;
        }
        
        order.setStatus(status);
        
        // 如果状态为已完成，更新支付状态为已支付
        if ("COMPLETED".equals(status)) {
            order.setPaymentStatus("PAID");
        }
        
        return updateById(order);
    }
    
    /**
     * 取消订单
     *
     * @param id     订单ID
     * @param reason 取消原因
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelOrder(Long id, String reason) {
        Order order = getById(id);
        if (order == null) {
            return false;
        }
        
        // 设置订单状态为已取消
        order.setStatus("CANCELLED");
        
        // 如果有取消原因，则设置到备注中
        if (StrUtil.isNotBlank(reason)) {
            // 如果之前有备注，则追加
            String remark = order.getRemark();
            if (StrUtil.isNotBlank(remark)) {
                order.setRemark(remark + " | 取消原因：" + reason);
            } else {
                order.setRemark("取消原因：" + reason);
            }
        }
        
        return updateById(order);
    }
    
    /**
     * 支付订单
     *
     * @param id            订单ID
     * @param paymentMethod 支付方式
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean payOrder(Long id, String paymentMethod) {
        // 验证支付方式
        if (!isValidPaymentMethod(paymentMethod)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的支付方式");
        }
        
        Order order = getById(id);
        if (order == null) {
            return false;
        }
        
        // 如果订单已支付，则返回失败
        if ("PAID".equals(order.getPaymentStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已支付，请勿重复支付");
        }
        
        // 设置支付方式和支付状态
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PAID");
        
        // 更新订单状态为已支付
        order.setStatus("PAID");
        
        return updateById(order);
    }
    
    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 是否成功
     */
    @Override
    public boolean deleteOrder(Long id) {
        return removeById(id);
    }
    
    /**
     * 检查用户是否在特定商家有已完成订单
     *
     * @param userId     用户ID
     * @param merchantId 商家ID
     * @return 是否有已完成订单
     */
    @Override
    public boolean hasCompletedOrderWithMerchant(Long userId, Integer merchantId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                   .eq(Order::getMerchantId, merchantId)
                   .eq(Order::getStatus, "COMPLETED")
                   .eq(Order::getIsDeleted, 0)
                   .last("LIMIT 1");
                   
        return count(queryWrapper) > 0;
    }
    
    /**
     * 生成唯一的订单号
     *
     * @return 订单号
     */
    private String generateOrderNumber() {
        // 生成格式：年月日 + 13位雪花算法ID
        return "ORD" + System.currentTimeMillis() + IdUtil.getSnowflakeNextIdStr().substring(0, 5);
    }
    
    /**
     * 验证订单状态是否有效
     *
     * @param status 订单状态
     * @return 是否有效
     */
    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) ||
               "PAID".equals(status) ||
               "PREPARING".equals(status) ||
               "DELIVERING".equals(status) ||
               "COMPLETED".equals(status) ||
               "CANCELLED".equals(status);
    }
    
    /**
     * 验证支付方式是否有效
     *
     * @param paymentMethod 支付方式
     * @return 是否有效
     */
    private boolean isValidPaymentMethod(String paymentMethod) {
        return "WECHAT".equals(paymentMethod) ||
               "ALIPAY".equals(paymentMethod) ||
               "CASH".equals(paymentMethod);
    }
}




