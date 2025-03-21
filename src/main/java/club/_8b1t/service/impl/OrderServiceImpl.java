package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Order;
import club._8b1t.service.OrderService;
import club._8b1t.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author root
* @description 针对表【order(订单信息表)】的数据库操作Service实现
* @createDate 2025-03-16 16:07:17
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




