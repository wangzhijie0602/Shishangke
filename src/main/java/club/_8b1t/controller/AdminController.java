package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.dto.merchant.MerchantUpdateRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.enums.MerchantStatusEnum;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.MerchantService;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

public class AdminController {

    @Resource
    private UserService userService;
    
    @Resource
    private MerchantService merchantService;

    @Resource
    private Converter converter;

    /**
     * 管理员查询商家列表
     */
    @PostMapping("/merchant/list")
    public Result<Page<MerchantVO>> getMerchantList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestBody(required = false) MerchantQueryRequest request) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        
        if (request != null) {
            // 根据用户ID筛选
            wrapper.eq(request.getUserId() != null, Merchant::getUserId, request.getUserId());
            // 根据商家名称模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getName()), Merchant::getName, request.getName());
            // 根据商家电话模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getPhone()), Merchant::getPhone, request.getPhone());
            // 根据状态筛选
            wrapper.eq(StrUtil.isNotBlank(request.getStatus()), Merchant::getStatus, request.getStatus());
        }
        
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);
        
        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));
        
        return ResultUtil.success(merchantVOList);
    }
    
    /**
     * 管理员获取商家详情
     */
    @GetMapping("/merchant/{id}")
    public Result<MerchantVO> getMerchant(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        return ResultUtil.success(converter.convert(merchant, MerchantVO.class));
    }
    
    /**
     * 管理员完整更新商家信息
     */
    @PostMapping("/merchant/update")
    public Result<String> updateMerchant(@RequestBody @Valid MerchantUpdateRequest request) {
        // 验证商家是否存在
        Merchant merchant = merchantService.getById(request.getId());
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        // 将请求参数转换为商家对象
        Merchant updatedMerchant = converter.convert(request, Merchant.class);
        
        // 更新商家信息
        boolean updated = merchantService.updateMerchantInfo(updatedMerchant);
        
        // 如果更新失败，抛出系统异常
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("更新成功");
    }
    
    /**
     * 管理员更新商家名称
     */
    @PostMapping("/merchant/{id}/update-name")
    public Result<String> updateMerchantName(@PathVariable Long id, @RequestParam String name) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updateName(id, name);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("名称更新成功");
    }
    
    /**
     * 管理员更新商家Logo
     */
    @PostMapping("/merchant/{id}/update-logo")
    public Result<String> updateMerchantLogo(@PathVariable Long id, @RequestParam String logo) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
//        boolean updated = merchantService.updateLogo(id, logo);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("Logo更新成功");
    }
    
    /**
     * 管理员更新商家联系电话
     */
    @PostMapping("/merchant/{id}/update-phone")
    public Result<String> updateMerchantPhone(@PathVariable Long id, @RequestParam String phone) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updatePhone(id, phone);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("联系电话更新成功");
    }
    
    /**
     * 管理员更新商家地址
     */
    @PostMapping("/merchant/{id}/update-address")
    public Result<String> updateMerchantAddress(@PathVariable Long id, 
                                              @RequestParam String province,
                                              @RequestParam String city,
                                              @RequestParam String district,
                                              @RequestParam String street,
                                              @RequestParam String addressDetail) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updateAddress(id, province, city, district, street, addressDetail);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("地址更新成功");
    }
    
    /**
     * 管理员更新商家营业时间
     */
    @PostMapping("/merchant/{id}/update-business-hours")
    public Result<String> updateMerchantBusinessHours(@PathVariable Long id, 
                                                    @RequestParam String openTime,
                                                    @RequestParam String closeTime) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updateBusinessHours(id, openTime, closeTime);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("营业时间更新成功");
    }
    
    /**
     * 管理员更新商家描述
     */
    @PostMapping("/merchant/{id}/update-description")
    public Result<String> updateMerchantDescription(@PathVariable Long id, @RequestParam String description) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updateDescription(id, description);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("描述更新成功");
    }
    
    /**
     * 管理员更新商家最低起送价
     */
    @PostMapping("/merchant/{id}/update-min-price")
    public Result<String> updateMerchantMinPrice(@PathVariable Long id, @RequestParam BigDecimal minPrice) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean updated = merchantService.updateMinPrice(id, minPrice);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }
        
        return ResultUtil.success("最低起送价更新成功");
    }
    
    /**
     * 管理员更新商家状态
     */
    @PostMapping("/merchant/{id}/update-status")
    public Result<String> updateMerchantStatus(@PathVariable Long id, @RequestParam String status) {
        // 先确认商家是否存在
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        // 验证状态有效性
        if (!MerchantStatusEnum.isValidCode(status)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的商家状态");
        }
        
        MerchantStatusEnum statusEnum = MerchantStatusEnum.getByCode(status);
        boolean updated = merchantService.updateStatus(id, statusEnum);
        
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新失败");
        }

        return ResultUtil.success("状态更新成功");
    }
    
    /**
     * 管理员删除商家
     */
    @GetMapping("/merchant/{id}/delete")
    public Result<String> deleteMerchant(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在");
        }
        
        boolean deleted = merchantService.removeById(id);
        if (!deleted) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "删除失败");
        }
        
        return ResultUtil.success("删除成功");
    }
}
