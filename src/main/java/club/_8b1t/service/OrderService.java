package club._8b1t.service;

import club._8b1t.model.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 订单ID
     */
    Long createOrder(Order order);
    
    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @param userId 用户ID
     * @return 订单信息
     */
    Order getOrderByIdAndUserId(Long id, Long userId);
    
    /**
     * 更新订单状态
     *
     * @param id 订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    boolean updateOrderStatus(Long id, String status);
    
    /**
     * 取消订单
     *
     * @param id 订单ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long id, String reason);
    
    /**
     * 支付订单
     *
     * @param id 订单ID
     * @param paymentMethod 支付方式
     * @return 是否成功
     */
    boolean payOrder(Long id, String paymentMethod);
    
    /**
     * 删除订单（软删除）
     *
     * @param id 订单ID
     * @return 是否成功
     */
    boolean deleteOrder(Long id);
    
    /**
     * 检查用户是否在特定商家有已完成订单
     *
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @return 是否有已完成订单
     */
    boolean hasCompletedOrderWithMerchant(Long userId, Integer merchantId);
}
