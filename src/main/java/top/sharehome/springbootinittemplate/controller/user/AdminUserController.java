package top.sharehome.springbootinittemplate.controller.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.OperatorEnum;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserAddDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserPageDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserResetPasswordDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserUpdateInfoDto;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserPageVo;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.util.List;

/**
 * 管理员控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminUserController {

    @Resource
    private UserService userService;

    /**
     * 管理员分页查询用户信息
     *
     * @param adminUserPageDto 用户信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询用户信息", operator = OperatorEnum.QUERY)
    public R<Page<AdminUserPageVo>> pageUser(AdminUserPageDto adminUserPageDto, PageModel pageModel) {
        Page<AdminUserPageVo> page = userService.adminPageUser(adminUserPageDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员添加用户
     *
     * @param adminUserAddDto 被添加用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @ControllerLog(description = "管理员添加用户信息", operator = OperatorEnum.INSERT)
    public R<String> addUser(@RequestBody @Validated({PostGroup.class}) AdminUserAddDto adminUserAddDto) {
        userService.adminAddUser(adminUserAddDto);
        return R.ok("添加成功");
    }

    /**
     * 管理员根据ID删除用户
     *
     * @param id 被删除用户的ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除用户信息", operator = OperatorEnum.DELETE)
    public R<String> deleteUser(@PathVariable("id") Long id) {
        userService.adminDeleteUser(id);
        return R.ok("删除成功");
    }

    /**
     * 管理员修改用户信息
     *
     * @param adminUserUpdateInfoDto 被修改后的用户信息
     * @return 修改结果
     */
    @PutMapping("/update/info")
    @ControllerLog(description = "管理员修改用户信息", operator = OperatorEnum.UPDATE)
    public R<String> updateInfo(@RequestBody @Validated({PutGroup.class}) AdminUserUpdateInfoDto adminUserUpdateInfoDto) {
        userService.adminUpdateInfo(adminUserUpdateInfoDto);
        return R.ok("修改信息成功");
    }

    /**
     * 管理员修改用户状态
     *
     * @param id 被修改用户的ID
     * @return 修改结果
     */
    @PutMapping("/update/state/{id}")
    @ControllerLog(description = "管理员修改用户状态", operator = OperatorEnum.UPDATE)
    public R<String> updateState(@PathVariable("id") Long id) {
        userService.adminUpdateState(id);
        return R.ok("修改状态成功");
    }

    /**
     * 管理员重置用户密码
     *
     * @param adminUserResetPasswordDto 被重置密码信息
     * @return 重置结果
     */
    @PutMapping("/reset/password")
    @ControllerLog(description = "管理员重置用户密码", operator = OperatorEnum.UPDATE)
    public R<String> resetPassword(@RequestBody @Validated({PutGroup.class}) AdminUserResetPasswordDto adminUserResetPasswordDto) {
        userService.adminResetPassword(adminUserResetPasswordDto);
        return R.ok("重置密码成功");
    }

    /**
     * 导出用户表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    @ControllerLog(description = "管理员导出用户表格", operator = OperatorEnum.EXPORT)
    public R<Void> exportExcel(HttpServletResponse response) {
        List<AdminUserExportVo> list = userService.adminExportExcelList();
        ExcelUtils.exportHttpServletResponse(list, "用户表", AdminUserExportVo.class, response);
        return R.empty();
    }

}
