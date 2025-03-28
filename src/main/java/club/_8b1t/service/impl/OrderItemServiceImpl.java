package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.OrderItem;
import club._8b1t.service.OrderItemService;
import generator.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author root
* @description 针对表【order_item(订单项表)】的数据库操作Service实现
* @createDate 2025-03-28 15:38:35
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




