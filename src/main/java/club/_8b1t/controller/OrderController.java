package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.order.OrderAddressRequest;
import club._8b1t.model.dto.order.OrderCreateCompleteRequest;
import club._8b1t.model.dto.order.OrderQueryRequest;
import club._8b1t.model.dto.refund.RefundCreateRequest;
import club._8b1t.model.entity.*;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.enums.payment.PaymentStatus;
import club._8b1t.model.vo.OrderAndItemVO;
import club._8b1t.model.vo.OrderItemVO;
import club._8b1t.model.vo.OrderVO;
import club._8b1t.service.*;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static club._8b1t.exception.ResultCode.*;

/**
 * 订单控制器
 * 处理订单相关操作，包括订单的创建、查询、取消以及退款等功能
 * @author root
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Resource
    private OrderService orderService;
    
    @Resource
    private OrderItemService orderItemService;
    
    @Resource
    private MenuService menuService;
    
    @Resource
    private Converter converter;
    
    @Resource
    private MerchantService merchantService;
    
    @Resource
    private RefundService refundService;
    
    @Autowired
    private PaymentService paymentService;

    /**
     * 查询订单列表
     *
     * @param pageNum  页码，默认为1
     * @param pageSize 每页大小，默认为50
     * @param request  订单查询请求参数
     * @return 订单分页数据
     */
    @PostMapping("/orders")
    @Operation(operationId = "order_list_orders")
    public Result<Page<OrderVO>> listOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "50") Integer pageSize,
                                            @RequestBody(required = false) OrderQueryRequest request) {
        // 创建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，如果请求参数不为空
        if (request != null) {
            queryWrapper.eq(StrUtil.isNotBlank(request.getCustomerId()), Order::getCustomerId, request.getCustomerId());
            queryWrapper.eq(StrUtil.isNotBlank(request.getMerchantId()), Order::getMerchantId, request.getMerchantId());
            queryWrapper.eq(request.getStatus() != null, Order::getStatus, request.getStatus());
        }

        // 执行分页查询
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderService.page(page, queryWrapper);

        // 转换为VO对象
        Page<OrderVO> orderVOPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        orderVOPage.setRecords(converter.convert(orderPage.getRecords(), OrderVO.class));

        return ResultUtil.success(orderVOPage);
    }

    /**
     * 获取订单及其项目详情
     *
     * @param id 订单ID
     * @return 订单和订单项信息
     */
    @GetMapping("/{id}/orderanditem")
    @Operation(operationId = "order_get_order_and_item")
    public Result<OrderAndItemVO> getOrderAndItem(@PathVariable String id) {
        // 创建返回对象
        OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
        
        // 获取订单信息
        Order order = orderService.getById(id);
        orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));

        // 获取订单项信息
        List<OrderItem> orderItems = orderItemService.list(
                new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, order.getId())
        );
        orderAndItemVO.setOrderItem(converter.convert(orderItems, OrderItemVO.class));

        return ResultUtil.success(orderAndItemVO);
    }

    /**
     * 创建订单
     *
     * @param request 订单创建请求
     * @return 创建的订单和订单项信息
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @Operation(operationId = "order_create_order")
    public Result<OrderAndItemVO> createOrder(@RequestBody OrderCreateCompleteRequest request) {
        // 转换订单基本信息
        Order order = converter.convert(request.getOrderRequest(), Order.class);

        // 获取商家信息并设置商家名称
        Merchant merchant = merchantService.getById(order.getMerchantId());
        order.setMerchantName(merchant.getName());

        // 转换订单项信息
        List<OrderItem> orderItemList = converter.convert(request.getOrderItemRequests(), OrderItem.class);

        // 获取所有菜单项信息
        List<Menu> menuList = menuService.list(new LambdaQueryWrapper<>(Menu.class)
                .in(Menu::getMenuId, orderItemList.stream()
                        .map(OrderItem::getMenuId)
                        .collect(Collectors.toList())));

        // 构建菜单ID到菜单对象的映射
        Map<Long, Menu> menuMap = new HashMap<>(menuList.size());
        for (Menu menu : menuList) {
            menuMap.put(menu.getMenuId(), menu);
        }

        // 计算订单总金额
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (OrderItem orderItem : orderItemList) {
            Menu menu = menuMap.get(orderItem.getMenuId());
            // 设置订单项的详细信息
            orderItem.setMenuName(menu.getName());
            orderItem.setImageUrl(menu.getImageUrl());
            orderItem.setPrice(menu.getPrice());
            // 计算单个订单项总价：单价 * 数量
            orderItem.setTotalPrice(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            // 累加总金额
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        // 计算配送费（订单总额的10%，最高5元）
        BigDecimal deliveryFee = totalAmount.multiply(new BigDecimal("0.1")).min(new BigDecimal("5.00"));
        // 计算实际支付金额（总金额 + 配送费）
        BigDecimal actualAmount = totalAmount.add(deliveryFee);

        // 设置订单金额信息
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setActualAmount(actualAmount);

        // 计算预计送达时间（当前时间 + 1小时）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        order.setExpectedDeliveryTime(calendar.getTime());

        // 保存订单
        boolean saved = orderService.save(order);
        ExceptionUtil.throwIfNot(saved, OPERATION_FAILED);

        // 设置订单项的订单ID并保存
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
        });

        // 批量保存订单项
        boolean savedBatch = orderItemService.saveBatch(orderItemList);
        ExceptionUtil.throwIfNot(savedBatch, OPERATION_FAILED);

        // 构建返回对象
        OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
        orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));
        orderAndItemVO.setOrderItem(converter.convert(orderItemList, OrderItemVO.class));

        return ResultUtil.success(orderAndItemVO);
    }

    /**
     * 分页查询订单及其项目列表
     *
     * @param pageNum  页码，默认为1
     * @param pageSize 每页大小，默认为50
     * @param request  订单查询请求参数
     * @return 订单及订单项分页数据
     */
    @PostMapping("orderitems")
    @Operation(operationId = "order_list_order_items")
    public Result<Page<OrderAndItemVO>> listOrderAndItem(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "50") Integer pageSize,
                                                        @RequestBody(required = false) OrderQueryRequest request) {
        // 创建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，如果请求参数不为空
        if (request != null) {
            queryWrapper.eq(StrUtil.isNotBlank(request.getCustomerId()), Order::getCustomerId, request.getCustomerId());
            queryWrapper.eq(StrUtil.isNotBlank(request.getMerchantId()), Order::getMerchantId, request.getMerchantId());
            queryWrapper.eq(request.getStatus() != null, Order::getStatus, request.getStatus());
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(Order::getCreatedAt);

        // 执行分页查询
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderService.page(page, queryWrapper);

        // 创建返回分页对象
        Page<OrderAndItemVO> orderAndItemVOPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        orderAndItemVOPage.setRecords(new ArrayList<>());
        
        // 遍历订单，获取订单项并组装数据
        orderPage.getRecords().forEach(order -> {
            OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
            orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));
            
            // 查询每个订单对应的订单项
            List<OrderItem> orderItemList = orderItemService.list(
                    new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, order.getId())
            );
            orderAndItemVO.setOrderItem(converter.convert(orderItemList, OrderItemVO.class));
            
            // 添加到结果集
            orderAndItemVOPage.getRecords().add(orderAndItemVO);
        });

        return ResultUtil.success(orderAndItemVOPage);
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @PostMapping("/{id}/cancel")
    @Operation(operationId = "order_cancel_order")
    public Result<Void> cancelOrder(@PathVariable String id) {
        // 获取订单信息
        Order order = orderService.getById(id);
        ExceptionUtil.throwIfNull(order, OPERATION_FAILED);
        
        // 验证订单状态是否为待支付
        ExceptionUtil.throwIfNot(order.getStatus().equals(OrderStatus.PENDING), FORBIDDEN, "只能取消待支付的订单，请申请退款");
        
        // 更新订单状态为已取消
        order.setStatus(OrderStatus.CANCELLED);
        boolean success = orderService.updateById(order);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);

        return ResultUtil.success();
    }

    /**
     * 更改订单地址
     *
     * @param request 订单地址更新请求
     * @return 操作结果
     */
    @PostMapping("/changeaddress")
    @Operation(operationId = "order_change_address")
    public Result<Void> changeAddress(@RequestBody OrderAddressRequest request) {
        // 转换请求为订单对象
        Order order = converter.convert(request, Order.class);
        
        // 验证订单是否存在
        ExceptionUtil.throwIfNull(orderService.getById(order.getId()), OPERATION_FAILED);

        // 更新订单
        boolean success = orderService.updateById(order);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);

        return ResultUtil.success();
    }

    /**
     * 申请订单退款
     *
     * @param request 退款创建请求
     * @return 退款ID
     */
    @PostMapping("/refund")
    @Operation(operationId = "order_refund_order")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> refundOrder(@RequestBody RefundCreateRequest request) {
        // 转换请求为退款对象
        Refund refund = converter.convert(request, Refund.class);

        // 获取订单信息
        Order order = orderService.getById(refund.getOrderId());
        ExceptionUtil.throwIfNull(order, OPERATION_FAILED);
        
        // 更新订单状态为退款中
        order.setStatus(OrderStatus.REFUNDING);

        // 获取当前登录用户ID
        Long customerId = StpUtil.getLoginIdAsLong();
        // 验证用户是否为订单所有者
        ExceptionUtil.throwIfNot(customerId.equals(order.getCustomerId()), FORBIDDEN);

        // 验证订单状态不是待支付或已取消
        ExceptionUtil.throwIf(order.getStatus().equals(OrderStatus.PENDING) || order.getStatus().equals(OrderStatus.CANCELLED), FORBIDDEN);

        // 查询成功支付的支付记录
        Payment payment = paymentService.getOne(new LambdaQueryWrapper<>(Payment.class)
                .eq(Payment::getOrderId, order.getId())
                .eq(Payment::getCustomerId, customerId)
                .eq(Payment::getStatus, PaymentStatus.SUCCESS));
                
        // 验证支付记录存在
        ExceptionUtil.throwIfNull(payment, NOT_FOUND, "找不到对应的支付记录");
        
        // 验证退款金额不大于支付金额
        ExceptionUtil.throwIfNot(refund.getRefundAmount().compareTo(payment.getPaymentAmount()) > 0, FORBIDDEN, "不能申请大于订单的实付金额");

        // 设置退款所属用户和支付ID
        refund.setCustomerId(customerId);
        refund.setPaymentId(payment.getId());
        
        // 保存退款申请和更新订单状态
        boolean refundSuccess = refundService.save(refund);
        boolean orderSuccess = orderService.updateById(order);
        ExceptionUtil.throwIfNot(refundSuccess && orderSuccess, OPERATION_FAILED);
        
        return ResultUtil.success(refund.getId().toString());
    }
}
