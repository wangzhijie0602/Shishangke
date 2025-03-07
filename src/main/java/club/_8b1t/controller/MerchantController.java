package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.merchant.MerchantCreateRequest;
import club._8b1t.model.dto.merchant.MerchantQueryRequest;
import club._8b1t.model.dto.merchant.MerchantUpdateRequest;
import club._8b1t.model.entity.Merchant;
import club._8b1t.service.MerchantService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final Converter converter;

    @GetMapping("/list")
    public Result<Page<Merchant>> getMerchantList(@RequestParam(defaultValue = "1") Integer pageNum,
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
        return ResultUtil.success(merchantList);

    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody @Valid MerchantCreateRequest request) {

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
        // 查询当前用户ID对应的商家记录数量
        long l = merchantService.count(new LambdaQueryWrapper<>(Merchant.class)
                .eq(Merchant::getUserId, userId)
                .eq(Merchant::getId, request.getId()));
        // 如果没有找到对应的商家记录，抛出异常
        if (l == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 将请求参数转换为商家对象
        Merchant merchant = converter.convert(request, Merchant.class);
        // 更新商家信息
        boolean updated = merchantService.updateById(merchant);
        // 如果更新失败，抛出系统异常
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 返回更新成功的响应
        return ResultUtil.success("更新成功");

    }

    @GetMapping("/{id}/delete")
    public Result<String> delete(@PathVariable String id) {
        // 获取当前登录用户的ID
        long userId = StpUtil.getLoginIdAsLong();
        // 查询当前用户ID对应的商家记录数量
        long l = merchantService.count(new LambdaQueryWrapper<>(Merchant.class)
                .eq(Merchant::getUserId, userId)
                .eq(Merchant::getId, id));
        if (l == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
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

}
