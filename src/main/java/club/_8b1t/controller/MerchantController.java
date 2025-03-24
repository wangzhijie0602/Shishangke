package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.dto.merchant.MerchantCreateRequest;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.dto.merchant.MerchantUpdateRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.enums.MerchantStatusEnum;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static club._8b1t.exception.ResultCode.*;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @Resource
    private Converter converter;

    /**
     * 验证商家所有权的私有方法
     *
     * @param merchantId 商家ID
     * @return 验证通过的商家对象
     * @throws BusinessException 如果商家不存在或用户无权操作该商家
     */
    private Merchant validateMerchantOwnership(Long merchantId) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || !merchant.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商家不存在或您无权修改");
        }
        return merchant;
    }

    @PostMapping("/list")
    public Result<Page<MerchantVO>> getMerchantList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestBody(required = false) MerchantQueryRequest request) {

        // 获取当前登录用户的ID
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>(Merchant.class)
                .eq(Merchant::getUserId, userId);

        if (request != null) {
            wrapper.like(StrUtil.isNotBlank(request.getName()), Merchant::getName, request.getName())
                    .like(StrUtil.isNotBlank(request.getPhone()), Merchant::getPhone, request.getPhone())
                    .eq(StrUtil.isNotBlank(request.getStatus()) && MerchantStatusEnum.isValidCode(request.getStatus()),
                            Merchant::getStatus, MerchantStatusEnum.getByCode(request.getStatus()));
        }

        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);

        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOList);

    }

    // todo
    @PostMapping("/create")
    public Result<Long> create(@RequestBody @Valid MerchantCreateRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = converter.convert(request, Merchant.class);
        merchant.setUserId(userId);
        boolean saved = merchantService.save(merchant);
        ExceptionUtil.throwIfNot(saved, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("创建成功", merchant.getId());
    }

    @PostMapping("/{id}/update/name")
    public Result<Void> updateName(@PathVariable Long id, @RequestParam String name) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateName(id, name);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED, "更新失败");
        return ResultUtil.success("名称更新成功");
    }

    @PostMapping("/{id}/update/logo")
    public Result<Void> updateLogo(@PathVariable Long id,
                                   @RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateLogo(id, file);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("Logo更新成功");
    }

    @PostMapping("/{id}/update/phone")
    public Result<Void> updatePhone(@PathVariable Long id, @RequestParam String phone) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updatePhone(id, phone);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("联系电话更新成功");
    }

    @PostMapping("/{id}/update/address")
    public Result<Void> updateAddress(@PathVariable Long id,
                                      @RequestParam String province,
                                      @RequestParam String city,
                                      @RequestParam String district,
                                      @RequestParam String street,
                                      @RequestParam String addressDetail) {

        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateAddress(id, province, city, district, street, addressDetail);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("地址更新成功");
    }

    @PostMapping("/{id}/update/businesshours")
    public Result<Void> updateBusinessHours(@PathVariable Long id,
                                            @RequestParam String openTime,
                                            @RequestParam String closeTime) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateBusinessHours(id, openTime, closeTime);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("营业时间更新成功");
    }

    @PostMapping("/{id}/update/description")
    public Result<Void> updateDescription(@PathVariable Long id, @RequestParam String description) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateDescription(id, description);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("描述更新成功");
    }

    @PostMapping("/{id}/update/minprice")
    public Result<Void> updateMinPrice(@PathVariable Long id, @RequestParam BigDecimal minPrice) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateMinPrice(id, minPrice);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("最低起送价更新成功");
    }

    @PostMapping("/{id}/update/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        ExceptionUtil.throwIfNot(MerchantStatusEnum.isValidCode(status), BAD_REQUEST, "无效的商家状态");
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        // todo 商家可以给自己的店铺更改一些不可更改的状态
        boolean success = merchantService.updateStatus(id, MerchantStatusEnum.getByCode(status));
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("状态更新成功");
    }

    @GetMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable String id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.removeById(id);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("删除成功");
    }

    @GetMapping("/{id}/get")
    public Result<MerchantVO> getMerchant(@PathVariable String id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), FORBIDDEN);
        }
        MerchantVO merchantVO = converter.convert(merchant, MerchantVO.class);
        return ResultUtil.success(merchantVO);
    }
}
