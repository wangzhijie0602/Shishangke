package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Customer;
import club._8b1t.service.CustomerService;
import club._8b1t.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

/**
* @author root
* @description 针对表【customer(顾客信息表)】的数据库操作Service实现
* @createDate 2025-03-16 16:07:17
*/
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>
    implements CustomerService{

}




