package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Payment;
import club._8b1t.service.PaymentService;
import club._8b1t.mapper.PaymentMapper;
import org.springframework.stereotype.Service;

/**
* @author root
* @description 针对表【payment(支付表)】的数据库操作Service实现
* @createDate 2025-03-29 15:35:05
*/
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment>
    implements PaymentService{

}




