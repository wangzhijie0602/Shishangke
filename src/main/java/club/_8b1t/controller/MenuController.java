package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;
import club._8b1t.model.dto.menu.MenuCreateRequest;
import club._8b1t.model.dto.menu.MenuQueryRequest;
import club._8b1t.model.dto.menu.MenuUpdateRequest;
import club._8b1t.model.entity.Menu;
import club._8b1t.model.vo.MenuVO;
import club._8b1t.service.CosService;
import club._8b1t.service.MenuService;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.util.ResultUtil;
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

import static club._8b1t.exception.ResultCode.OPERATION_FAILED;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private Converter converter;

    @Resource
    private CosService cosService;

    /**
     * 分页查询菜单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param request  查询条件
     * @return 菜单列表
     */
    @PostMapping("/list")
    @Operation(operationId = "menu_list")
    public Result<Page<MenuVO>> getMenuList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestBody(required = false) MenuQueryRequest request) {

        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();

        if (request != null) {
            // 根据商家ID查询
            wrapper.eq(request.getMerchantId() != null, Menu::getMerchantId, request.getMerchantId());
            // 根据菜品名称模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getName()), Menu::getName, request.getName());
            // 根据菜品分类查询
            wrapper.eq(request.getCategory() != null, Menu::getCategory, request.getCategory());
            // 根据菜品状态查询
            wrapper.eq(request.getStatus() != null, Menu::getStatus, request.getStatus());
            // 根据价格范围查询
            wrapper.ge(request.getMinPrice() != null, Menu::getPrice, request.getMinPrice());
            wrapper.le(request.getMaxPrice() != null, Menu::getPrice, request.getMaxPrice());
        }

        // 按照排序权重升序排列
        wrapper.orderByAsc(Menu::getSortOrder);

        Page<Menu> page = new Page<>(pageNum, pageSize);
        Page<Menu> menuList = menuService.page(page, wrapper);

        Page<MenuVO> menuVOList = new Page<>(menuList.getCurrent(), menuList.getSize(), menuList.getTotal());
        menuVOList.setRecords(converter.convert(menuList.getRecords(), MenuVO.class));

        return ResultUtil.success(menuVOList);
    }

    /**
     * 创建菜单项
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/create")
    @Operation(operationId = "menu_create")
    public Result<String> create(@RequestBody @Valid MenuCreateRequest request) {
        // 将请求参数转换为菜单对象
        Menu menu = converter.convert(request, Menu.class);
        // 保存菜单信息
        boolean success = menuService.save(menu);
        // 如果保存失败，抛出系统异常
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);
        // 返回创建成功的响应
        return ResultUtil.success("创建成功", menu.getMenuId().toString());
    }

    /**
     * 更新菜单项
     *
     * @param request 更新请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(operationId = "menu_update")
    public Result<String> update(@RequestBody @Valid MenuUpdateRequest request) {
        // 检查菜单是否存在
        Menu existMenu = menuService.getById(request.getMenuId());
        if (existMenu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单项不存在");
        }

        // 将请求参数转换为菜单对象
        Menu menu = converter.convert(request, Menu.class);
        // 更新菜单信息
        boolean updated = menuService.updateById(menu);
        // 如果更新失败，抛出系统异常
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR);
        }
        // 返回更新成功的响应
        return ResultUtil.success("更新成功");
    }

    /**
     * 删除菜单项
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    @GetMapping("/{id}/delete")
    @Operation(operationId = "menu_delete")
    public Result<String> delete(@PathVariable String id) {
        // 检查菜单是否存在
        Menu existMenu = menuService.getById(id);
        if (existMenu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单项不存在");
        }

        // 删除菜单信息
        boolean deleted = menuService.removeById(id);
        // 如果删除失败，抛出系统异常
        if (!deleted) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR);
        }
        // 返回删除成功的响应
        return ResultUtil.success("删除成功");
    }

    /**
     * 获取菜单项详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}/get")
    @Operation(operationId = "menu_get")
    public Result<MenuVO> getMenu(@PathVariable String id) {
        // 获取菜单信息
        Menu menu = menuService.getById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "菜单项不存在");
        }

        // 转换为视图对象并返回
        return ResultUtil.success(converter.convert(menu, MenuVO.class));
    }

    /**
     * 根据商家ID获取菜单列表
     *
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param request 查询条件
     * @return 菜单列表
     */
    @PostMapping("/merchant/{merchantId}")
    @Operation(operationId = "menu_get_by_merchant")
    public Result<Page<MenuVO>> getMenuByMerchant(@PathVariable String merchantId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "50") Integer pageSize,
                                                  @RequestBody(required = false) MenuQueryRequest request) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMerchantId, merchantId)
                .eq(Menu::getIsDeleted, 0);

        if (request != null) {
            // 根据菜品名称模糊查询
            wrapper.like(StrUtil.isNotBlank(request.getName()), Menu::getName, request.getName());
            // 根据菜品分类查询
            wrapper.eq(request.getCategory() != null, Menu::getCategory, request.getCategory());
            // 根据菜品状态查询
            wrapper.eq(request.getStatus() != null, Menu::getStatus, request.getStatus());
            // 根据价格范围查询
            wrapper.ge(request.getMinPrice() != null, Menu::getPrice, request.getMinPrice());
            wrapper.le(request.getMaxPrice() != null, Menu::getPrice, request.getMaxPrice());
        }

        // 按照排序权重升序排列
        wrapper.orderByAsc(Menu::getSortOrder);

        Page<Menu> page = new Page<>(pageNum, pageSize);
        Page<Menu> menuList = menuService.page(page, wrapper);

        Page<MenuVO> menuVOList = new Page<>(menuList.getCurrent(), menuList.getSize(), menuList.getTotal());
        menuVOList.setRecords(converter.convert(menuList.getRecords(), MenuVO.class));

        return ResultUtil.success(menuVOList);
    }

    /**
     * 上传菜品图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    @Operation(operationId = "menu_upload_image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要上传的图片");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        String imageUrl = cosService.uploadMenuLogo(userId, file);
        return ResultUtil.success("图片上传成功", imageUrl);
    }
} 