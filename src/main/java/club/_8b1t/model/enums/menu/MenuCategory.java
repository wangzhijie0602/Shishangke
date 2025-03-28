package club._8b1t.model.enums.menu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 菜品分类枚举
 */
@Getter
public enum MenuCategory {
    /**
     * 热销菜品
     */
    HOT_SALE("HOT_SALE", "热销菜品"),
    
    /**
     * 特色菜品
     */
    SPECIALTY("SPECIALTY", "特色菜品"),
    
    /**
     * 主食
     */
    STAPLE_FOOD("STAPLE_FOOD", "主食"),
    
    /**
     * 小吃/点心
     */
    SNACK("SNACK", "小吃/点心"),
    
    /**
     * 汤品
     */
    SOUP("SOUP", "汤品"),
    
    /**
     * 凉菜
     */
    COLD_DISH("COLD_DISH", "凉菜"),
    
    /**
     * 热菜
     */
    HOT_DISH("HOT_DISH", "热菜"),
    
    /**
     * 甜点
     */
    DESSERT("DESSERT", "甜点"),
    
    /**
     * 饮料
     */
    BEVERAGE("BEVERAGE", "饮料"),
    
    /**
     * 酒水
     */
    ALCOHOL("ALCOHOL", "酒水"),
    
    /**
     * 套餐
     */
    SET_MEAL("SET_MEAL", "套餐"),
    
    /**
     * 其他
     */
    OTHER("OTHER", "其他");

    @EnumValue
    private final String code;
    private final String desc;

    MenuCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 分类编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static MenuCategory getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (MenuCategory category : MenuCategory.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        
        return null;
    }

    /**
     * 检查分类编码是否有效
     * 
     * @param code 分类编码
     * @return 是否是有效的分类编码
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
} 