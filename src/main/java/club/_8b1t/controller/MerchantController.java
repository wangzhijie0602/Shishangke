package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.merchant.MerchantCreateRequest;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.dto.merchant.MerchantUpdateRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @Resource
    private Converter converter;

    @PostMapping("/list")
    public Result<Page<MerchantVO>> getMerchantList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestBody(required = false) MerchantQueryRequest request) {

        // 获取当前登录用户的ID
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>(Merchant.class)
                .eq(Merchant::getUserId, userId);

        if (request != null) {
            wrapper.like(StrUtil.isNotBlank(request.getName()), Merchant::getName, request.getName())
                    .like(StrUtil.isNotBlank(request.getPhone()), Merchant::getPhone, request.getPhone())
                    .eq(StrUtil.isNotBlank(request.getStatus()), Merchant::getStatus, request.getStatus());
        }

        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);

        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOList);

    }

    @PostMapping("/create")
    public Result<Long> create(@RequestBody @Valid MerchantCreateRequest request) {

        // 使用StpUtil工具类获取当前登录用户的ID，并将其转换为long类型
        long userId = StpUtil.getLoginIdAsLong();
        // 使用converter对象将接收到的商家创建请求对象request转换为Merchant对象
        Merchant merchant = converter.convert(request, Merchant.class);
        // 设置商家对象的用户ID为当前登录用户的ID
        merchant.setUserId(userId);
        // 调用merchantService的save方法保存商家对象，返回保存是否成功的结果
        boolean saved = merchantService.save(merchant);
        // 如果保存失败，抛出一个业务异常，异常码为SYSTEM_ERROR
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 如果保存成功，返回一个成功的Result对象，包含消息"创建成功"和商家对象的ID
        return ResultUtil.success("创建成功", merchant.getId());
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody @Valid MerchantUpdateRequest request) {
        // 获取当前登录用户的ID
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(Long.valueOf(request.getId()), userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        // 将请求参数转换为商家对象
        Merchant updatedMerchant = converter.convert(request, Merchant.class);
        // 确保不修改商家所有者
        updatedMerchant.setUserId(userId);
        
        // 更新商家信息
        boolean updated = merchantService.updateMerchantInfo(updatedMerchant);
        
        // 如果更新失败，抛出系统异常
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        // 返回更新成功的响应
        return ResultUtil.success("更新成功");
    }
    
    /**
     * 更新商家名称
     */
    @PostMapping("/{id}/update-name")
    public Result<String> updateName(@PathVariable Long id, @RequestParam String name) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateName(id, name);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("名称更新成功");
    }
    
    /**
     * 更新商家Logo
     */
    @PostMapping("/{id}/update-logo")
    public Result<String> updateLogo(@PathVariable Long id, @RequestParam String logo) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateLogo(id, logo);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("Logo更新成功");
    }
    
    /**
     * 更新商家联系电话
     */
    @PostMapping("/{id}/update-phone")
    public Result<String> updatePhone(@PathVariable Long id, @RequestParam String phone) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updatePhone(id, phone);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("联系电话更新成功");
    }
    
    /**
     * 更新商家地址
     */
    @PostMapping("/{id}/update-address")
    public Result<String> updateAddress(@PathVariable Long id, 
                                        @RequestParam String province,
                                        @RequestParam String city,
                                        @RequestParam String district,
                                        @RequestParam String street,
                                        @RequestParam String addressDetail) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateAddress(id, province, city, district, street, addressDetail);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("地址更新成功");
    }
    
    /**
     * 更新商家营业时间
     */
    @PostMapping("/{id}/update-business-hours")
    public Result<String> updateBusinessHours(@PathVariable Long id, 
                                            @RequestParam String openTime,
                                            @RequestParam String closeTime) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateBusinessHours(id, openTime, closeTime);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("营业时间更新成功");
    }
    
    /**
     * 更新商家描述
     */
    @PostMapping("/{id}/update-description")
    public Result<String> updateDescription(@PathVariable Long id, @RequestParam String description) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateDescription(id, description);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("描述更新成功");
    }
    
    /**
     * 更新商家最低起送价
     */
    @PostMapping("/{id}/update-min-price")
    public Result<String> updateMinPrice(@PathVariable Long id, @RequestParam BigDecimal minPrice) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateMinPrice(id, minPrice);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("最低起送价更新成功");
    }
    
    /**
     * 更新商家状态
     */
    @PostMapping("/{id}/update-status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(id, userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }
        
        boolean updated = merchantService.updateStatus(id, status);
        
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        
        return ResultUtil.success("状态更新成功");
    }

    @GetMapping("/{id}/delete")
    public Result<String> delete(@PathVariable String id) {
        // 获取当前登录用户的ID
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(Long.valueOf(id), userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权修改");
        }

        // 删除商家信息
        boolean deleted = merchantService.removeById(id);
        // 如果删除失败，抛出系统异常
        if (!deleted) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 返回删除成功的响应
        return ResultUtil.success("删除成功");
    }

    @GetMapping("/{id}/get")
    public Result<MerchantVO> getMerchant(@PathVariable String id) {
        long userId = StpUtil.getLoginIdAsLong();
        
        // 验证商家所有权
        Merchant merchant = merchantService.getMerchantByIdAndUserId(Long.valueOf(id), userId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商家不存在或您无权查看");
        }

        return ResultUtil.success(converter.convert(merchant, MerchantVO.class));
    }
}
