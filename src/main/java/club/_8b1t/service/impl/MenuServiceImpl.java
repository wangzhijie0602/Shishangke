package club._8b1t.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Menu;
import club._8b1t.service.MenuService;
import club._8b1t.mapper.MenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 8bit
* @description 针对表【menu(菜单表)】的数据库操作Service实现
* @createDate 2025-03-12 13:19:34
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

    @Override
    public List<Menu> getMenuByMerchantId(Long merchantId) {
        return this.list(new LambdaQueryWrapper<>(Menu.class)
                .eq(Menu::getMerchantId, merchantId));
    }
}




