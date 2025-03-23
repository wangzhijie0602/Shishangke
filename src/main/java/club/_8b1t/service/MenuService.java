package club._8b1t.service;

import club._8b1t.model.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 8bit
* @description 针对表【menu(菜单表)】的数据库操作Service
* @createDate 2025-03-12 13:19:34
*/
public interface MenuService extends IService<Menu> {

    List<Menu> getMenuByMerchantId(Long merchantId);

}
