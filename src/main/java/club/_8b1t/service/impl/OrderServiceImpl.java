package club._8b1t.service.impl;

import club._8b1t.model.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.service.OrderService;
import club._8b1t.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author root
* @description 针对表【base_order】的数据库操作Service实现
* @createDate 2025-03-29 22:56:23
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService {

}




