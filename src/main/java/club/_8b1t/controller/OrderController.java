package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.order.OrderAddressRequest;
import club._8b1t.model.dto.order.OrderCreateCompleteRequest;
import club._8b1t.model.dto.order.OrderQueryRequest;
import club._8b1t.model.entity.Menu;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.entity.Order;
import club._8b1t.model.entity.OrderItem;
import club._8b1t.model.enums.order.OrderStatus;
import club._8b1t.model.vo.OrderAndItemVO;
import club._8b1t.model.vo.OrderItemVO;
import club._8b1t.model.vo.OrderVO;
import club._8b1t.service.MenuService;
import club._8b1t.service.MerchantService;
import club._8b1t.service.OrderItemService;
import club._8b1t.service.OrderService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
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

import static club._8b1t.exception.ResultCode.OPERATION_FAILED;

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
    @Autowired
    private MerchantService merchantService;

    @PostMapping("/orders")
    @Operation(operationId = "order_list_orders")
    public Result<Page<OrderVO>> listOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "50") Integer pageSize,
                                            @RequestBody(required = false) OrderQueryRequest request) {

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (request != null) {
            queryWrapper.eq(StrUtil.isNotBlank(request.getCustomerId()), Order::getCustomerId, request.getCustomerId());
            queryWrapper.eq(StrUtil.isNotBlank(request.getMerchantId()), Order::getMerchantId, request.getMerchantId());
            queryWrapper.eq(request.getStatus() != null, Order::getStatus, request.getStatus());
        }


        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderService.page(page, queryWrapper);

        Page<OrderVO> orderVOPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        orderVOPage.setRecords(converter.convert(orderPage.getRecords(), OrderVO.class));

        return ResultUtil.success(orderVOPage);

    }

    @GetMapping("/{id}/orderanditem")
    @Operation(operationId = "order_get_order_and_item")
    public Result<OrderAndItemVO> getOrderAndItem(@PathVariable String id) {

        OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
        Order order = orderService.getById(id);
        orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));

        List<OrderItem> orderItems = orderItemService.list(new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, order.getId()));
        orderAndItemVO.setOrderItem(converter.convert(orderItems, OrderItemVO.class));

        return ResultUtil.success(orderAndItemVO);

    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @Operation(operationId = "order_create_order")
    public Result<OrderAndItemVO> createOrder(@RequestBody OrderCreateCompleteRequest request) {

        Order order = converter.convert(request.getOrderRequest(), Order.class);

        Merchant merchant = merchantService.getById(order.getMerchantId());
        order.setMerchantName(merchant.getName());

        List<OrderItem> orderItemList = converter.convert(request.getOrderItemRequests(), OrderItem.class);

        List<Menu> menuList = menuService.list(new LambdaQueryWrapper<>(Menu.class)
                .in(Menu::getMenuId, orderItemList.stream()
                        .map(OrderItem::getMenuId)
                        .collect(Collectors.toList())));

        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu menu : menuList) {
            menuMap.put(menu.getMenuId(), menu);
        }

        // 计算应付价格
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (OrderItem orderItem : orderItemList) {
            Menu menu = menuMap.get(orderItem.getMenuId());
            orderItem.setMenuName(menu.getName());
            orderItem.setImageUrl(menu.getImageUrl());
            orderItem.setPrice(menu.getPrice());
            orderItem.setTotalPrice(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        BigDecimal deliveryFee = totalAmount.multiply(new BigDecimal("0.1")).min(new BigDecimal("5.00"));
        BigDecimal actualAmount = totalAmount.add(deliveryFee);

        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setActualAmount(actualAmount);

        // 计算预计送达时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        order.setExpectedDeliveryTime(calendar.getTime());

        boolean saved = orderService.save(order);
        ExceptionUtil.throwIfNot(saved, OPERATION_FAILED);

        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
        });

        boolean savedBatch = orderItemService.saveBatch(orderItemList);
        ExceptionUtil.throwIfNot(savedBatch, OPERATION_FAILED);

        OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
        orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));
        orderAndItemVO.setOrderItem(converter.convert(orderItemList, OrderItemVO.class));

        return ResultUtil.success(orderAndItemVO);

    }

    @PostMapping("orderitems")
    @Operation(operationId = "order_list_order_items")
    public Result<Page<OrderAndItemVO>> listOrderAndItem(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "50") Integer pageSize,
                                                        @RequestBody(required = false) OrderQueryRequest request) {

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (request != null) {
            queryWrapper.eq(StrUtil.isNotBlank(request.getCustomerId()), Order::getCustomerId, request.getCustomerId());
            queryWrapper.eq(StrUtil.isNotBlank(request.getMerchantId()), Order::getMerchantId, request.getMerchantId());
            queryWrapper.eq(request.getStatus() != null, Order::getStatus, request.getStatus());
        }

        queryWrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderService.page(page, queryWrapper);

        Page<OrderAndItemVO> orderAndItemVOPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        orderAndItemVOPage.setRecords(new ArrayList<>());
        orderPage.getRecords().forEach(order -> {
            OrderAndItemVO orderAndItemVO = new OrderAndItemVO();
            orderAndItemVO.setOrder(converter.convert(order, OrderVO.class));
            List<OrderItem> orderItemList = orderItemService.list(new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, order.getId()));
            orderAndItemVO.setOrderItem(converter.convert(orderItemList, OrderItemVO.class));
            orderAndItemVOPage.getRecords().add(orderAndItemVO);
        });

        return ResultUtil.success(orderAndItemVOPage);
    }

    @PostMapping("/{id}/cancel")
    @Operation(operationId = "order_cancel_order")
    public Result<Void> cancelOrder(@PathVariable String id) {

        Order order = orderService.getById(id);
        ExceptionUtil.throwIfNull(order, OPERATION_FAILED);

        order.setStatus(OrderStatus.CANCELLED);
        boolean success = orderService.updateById(order);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);

        return ResultUtil.success();
    }

    @PostMapping("/changeaddress")
    @Operation(operationId = "order_change_address")
    public Result<Void> changeAddress(@RequestBody OrderAddressRequest request) {

        Order order = converter.convert(request, Order.class);
        ExceptionUtil.throwIfNull(orderService.getById(order.getId()), OPERATION_FAILED);

        boolean success = orderService.updateById(order);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);

        return ResultUtil.success();

    }
}
