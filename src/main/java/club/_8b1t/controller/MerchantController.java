package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.dto.merchant.MerchantCreateRequest;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.model.enums.merchant.StatusEnum;
import club._8b1t.model.vo.MerchantVO;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/list")
    @Operation(operationId = "merchant_list")
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
                    .eq(StrUtil.isNotBlank(request.getStatus()) && StatusEnum.isValidCode(request.getStatus()),
                            Merchant::getStatus, StatusEnum.getByCode(request.getStatus()));
        }

        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);

        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOList);

    }

    @PostMapping("/create")
    @Operation(operationId = "merchant_create")
    public Result<Long> create(@RequestBody @Valid MerchantCreateRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = converter.convert(request, Merchant.class);
        merchant.setUserId(userId);
        boolean saved = merchantService.save(merchant);
        ExceptionUtil.throwIfNot(saved, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("创建成功", merchant.getId());
    }

    @PostMapping("/{id}/update/name")
    @Operation(operationId = "merchant_update_name")
    public Result<Void> updateName(@PathVariable String id, @RequestParam String name) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateName(Long.valueOf(id), name);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED, "更新失败");
        return ResultUtil.success("名称更新成功");
    }

    @PostMapping("/{id}/update/logo")
    @Operation(operationId = "merchant_update_logo")
    public Result<Void> updateLogo(@PathVariable String id,
                                   @RequestParam(name = "file", required = false) MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success;
        if (file != null) {
            success = merchantService.updateLogo(Long.valueOf(id), file);
        } else {
            merchant.setLogo("");
            success = merchantService.updateById(merchant);
        }

        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("Logo更新成功");
    }

    @PostMapping("/{id}/update/phone")
    @Operation(operationId = "merchant_update_phone")
    public Result<Void> updatePhone(@PathVariable String id, @RequestParam String phone) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updatePhone(Long.valueOf(id), phone);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("联系电话更新成功");
    }

    @PostMapping("/{id}/update/address")
    @Operation(operationId = "merchant_update_address")
    public Result<Void> updateAddress(@PathVariable String id,
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
        boolean success = merchantService.updateAddress(Long.valueOf(id), province, city, district, street, addressDetail);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("地址更新成功");
    }

    @PostMapping("/{id}/update/businesshours")
    @Operation(operationId = "merchant_update_businesshours")
    public Result<Void> updateBusinessHours(@PathVariable String id,
                                            @RequestParam String openTime,
                                            @RequestParam String closeTime) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateBusinessHours(Long.valueOf(id), openTime, closeTime);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("营业时间更新成功");
    }

    @PostMapping("/{id}/update/description")
    @Operation(operationId = "merchant_update_description")
    public Result<Void> updateDescription(@PathVariable String id, @RequestParam String description) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateDescription(Long.valueOf(id), description);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("描述更新成功");
    }

    @PostMapping("/{id}/update/minprice")
    @Operation(operationId = "merchant_update_minprice")
    public Result<Void> updateMinPrice(@PathVariable String id, @RequestParam BigDecimal minPrice) {
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        boolean success = merchantService.updateMinPrice(Long.valueOf(id), minPrice);
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("最低起送价更新成功");
    }

    @PostMapping("/{id}/update/status")
    @Operation(operationId = "merchant_update_status")
    public Result<Void> updateStatus(@PathVariable String id, @RequestParam String status) {
        ExceptionUtil.throwIfNot(StatusEnum.isValidCode(status), BAD_REQUEST, "无效的商家状态");
        Long userId = StpUtil.getLoginIdAsLong();
        Merchant merchant = merchantService.getById(id);
        ExceptionUtil.throwIfNull(merchant, BAD_REQUEST, "商家不存在");
        if (!StpUtil.hasRole("ADMIN")) {
            ExceptionUtil.throwIfNot(merchant.getUserId().equals(userId), BAD_REQUEST, "您无权修改");
        }
        // todo 商家可以给自己的店铺更改一些不可更改的状态
        boolean success = merchantService.updateStatus(Long.valueOf(id), StatusEnum.getByCode(status));
        ExceptionUtil.throwIfNot(success, ResultCode.OPERATION_FAILED);
        return ResultUtil.success("状态更新成功");
    }

    @GetMapping("/{id}/delete")
    @Operation(operationId = "merchant_delete")
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
    @Operation(operationId = "merchant_get")
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

    @PostMapping("/merchants")
    @Operation(operationId = "merchants")
    @SaCheckRole("ADMIN")
    public Result<Page<MerchantVO>> getMerchants(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestBody(required = false) MerchantQueryRequest request) {

        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>(Merchant.class);

        if (request != null) {
            wrapper.like(StrUtil.isNotBlank(request.getName()), Merchant::getName, request.getName())
                    .like(StrUtil.isNotBlank(request.getPhone()), Merchant::getPhone, request.getPhone())
                    .eq(StrUtil.isNotBlank(request.getStatus()) && StatusEnum.isValidCode(request.getStatus()),
                            Merchant::getStatus, StatusEnum.getByCode(request.getStatus()));
        }

        Page<Merchant> page = new Page<>(pageNum, pageSize);
        Page<Merchant> merchantList = merchantService.page(page, wrapper);

        Page<MerchantVO> merchantVOList = new Page<>(merchantList.getCurrent(), merchantList.getSize(), merchantList.getTotal());
        merchantVOList.setRecords(converter.convert(merchantList.getRecords(), MerchantVO.class));

        return ResultUtil.success(merchantVOList);

    }
}
