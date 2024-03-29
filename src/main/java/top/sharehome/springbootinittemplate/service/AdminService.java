package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminAddUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminPageUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminResetPasswordDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminExportVo;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminPageUserVo;

import java.util.List;

/**
 * 管理员服务接口
 *
 * @author AntonyCheng
 */
public interface AdminService extends IService<User> {

    /**
     * 管理员分页查询用户信息
     *
     * @param adminPageUserDto 用户信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    Page<AdminPageUserVo> pageUser(AdminPageUserDto adminPageUserDto, PageModel pageModel);

    /**
     * 管理员添加用户
     *
     * @param adminAddUserDto 被添加用户信息
     */
    void addUser(AdminAddUserDto adminAddUserDto);

    /**
     * 管理员根据ID删除用户
     *
     * @param id 被删除用户的ID
     */
    void deleteUser(Long id);

    /**
     * 管理员重置用户密码
     *
     * @param adminResetPasswordDto 被重置密码信息
     */
    void resetPassword(AdminResetPasswordDto adminResetPasswordDto);

    /**
     * 管理员导出用户表格
     * 此时导出的是逻辑未删除的数据，如果想要逻辑删除的数据：
     * 1、重新创建和User类mapper，service，controller相似的一个类，比如AUser，AUser的isDeleted字段打上TableLogic(value="false")，然后查询。（非常不推荐）
     * 2、在UserMapper当中使用@Select注解编写SQL查询。
     *
     * @return 用户列表结果
     */
    List<AdminExportVo> exportExcelList();
}
