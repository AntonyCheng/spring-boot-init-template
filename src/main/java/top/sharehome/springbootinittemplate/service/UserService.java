package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.model.dto.user.*;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserPageVo;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author AntonyCheng
 */
public interface UserService extends IService<User> {

    /**
     * 管理员分页查询用户信息
     *
     * @param adminUserPageDto 用户信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    Page<AdminUserPageVo> adminPageUser(AdminUserPageDto adminUserPageDto, PageModel pageModel);

    /**
     * 管理员添加用户
     *
     * @param adminUserAddDto 被添加用户信息
     */
    void adminAddUser(AdminUserAddDto adminUserAddDto);

    /**
     * 管理员根据ID删除用户
     *
     * @param id 被删除用户的ID
     */
    void adminDeleteUser(Long id);

    /**
     * 管理员修改用户信息
     *
     * @param adminUserUpdateInfoDto 被修改后的用户信息
     */
    void adminUpdateInfo(AdminUserUpdateInfoDto adminUserUpdateInfoDto);

    /**
     * 管理员修改用户状态
     *
     * @param adminUserUpdateStateDto 被修改用户的ID对象
     */
    void adminUpdateState(AdminUserUpdateStateDto adminUserUpdateStateDto);

    /**
     * 管理员重置用户密码
     *
     * @param adminUserResetPasswordDto 被重置密码信息
     */
    void adminResetPassword(AdminUserResetPasswordDto adminUserResetPasswordDto);

    /**
     * 管理员导出用户表格
     * 此时导出的是逻辑未删除的数据，如果想要逻辑删除的数据：
     * 1、重新创建和User类mapper，service，controller相似的一个类，比如AUser，AUser的isDeleted字段打上TableLogic(value="false")，然后查询。（非常不推荐）
     * 2、在UserMapper当中使用@Select注解编写SQL查询。
     *
     * @return 用户列表结果
     */
    List<AdminUserExportVo> adminExportExcelList();

    /**
     * 更新账号
     *
     * @param newAccount 新账号
     */
    void updateAccount(String newAccount);

    /**
     * 更新名称
     *
     * @param newName 新名称
     */
    void updateName(String newName);

    /**
     * 更新邮箱
     * @param newEmail 新邮箱
     */
    void updateEmail(String newEmail);

    /**
     * 更新密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(String oldPassword, String newPassword);

    /**
     * 更新头像
     *
     * @param file 新头像文件
     */
    void updateAvatar(MultipartFile file);

    /**
     * 导入用户信息表
     *
     * @param file 用户信息表文件
     */
    void adminImportUser(MultipartFile file);
}
