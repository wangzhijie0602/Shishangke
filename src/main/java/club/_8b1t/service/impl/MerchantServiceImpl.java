package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Merchant;
import club._8b1t.service.MerchantService;
import club._8b1t.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【merchant】的数据库操作Service实现
* @createDate 2025-03-07 21:08:39
*/
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
    implements MerchantService{

}




