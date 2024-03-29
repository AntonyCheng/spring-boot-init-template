package top.sharehome.springbootinittemplate.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminAddUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminPageUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminResetPasswordDto;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminExportVo;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminPageUserVo;
import top.sharehome.springbootinittemplate.service.AdminService;
import top.sharehome.springbootinittemplate.utils.excel.ExcelUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 管理员控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 管理员分页查询用户信息
     *
     * @param adminPageUserDto 用户信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    private R<Page<AdminPageUserVo>> pageUser(AdminPageUserDto adminPageUserDto, PageModel pageModel) {
        Page<AdminPageUserVo> page = adminService.pageUser(adminPageUserDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员添加用户
     *
     * @param adminAddUserDto 被添加用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    private R<String> add(@RequestBody @Validated({PostGroup.class}) AdminAddUserDto adminAddUserDto) {
        adminService.addUser(adminAddUserDto);
        return R.ok("添加成功");
    }

    /**
     * 管理员根据ID删除用户
     *
     * @param id 被删除用户的ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    private R<String> delete(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return R.ok("删除成功");
    }

    /**
     * 管理员重置用户密码
     *
     * @param adminResetPasswordDto 被重置密码信息
     * @return 重置结果
     */
    @PutMapping("/reset/password")
    private R<String> resetPassword(@RequestBody @Validated({PutGroup.class}) AdminResetPasswordDto adminResetPasswordDto) {
        adminService.resetPassword(adminResetPasswordDto);
        return R.ok("重置密码成功");
    }

    /**
     * 导出用户表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    private R<Void> exportExcel(HttpServletResponse response) {
        List<AdminExportVo> list = adminService.exportExcelList();
        ExcelUtils.exportHttpServletResponse(list, "用户表", AdminExportVo.class, response);
        return R.empty();
    }

}
