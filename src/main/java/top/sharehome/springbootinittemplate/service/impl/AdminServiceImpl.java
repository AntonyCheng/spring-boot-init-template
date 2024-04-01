package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeTransactionException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminAddUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminPageUserDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminResetPasswordDto;
import top.sharehome.springbootinittemplate.model.dto.admin.AdminUpdateInfoDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminExportVo;
import top.sharehome.springbootinittemplate.model.vo.admin.AdminPageUserVo;
import top.sharehome.springbootinittemplate.service.AdminService;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements AdminService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public Page<AdminPageUserVo> pageUser(AdminPageUserDto adminPageUserDto, PageModel pageModel) {
        Page<User> page = pageModel.build();
        Page<AdminPageUserVo> res = pageModel.build();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构造查询条件
        userLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(adminPageUserDto.getRole()), User::getRole, adminPageUserDto.getRole())
                .eq(Objects.nonNull(adminPageUserDto.getState()), User::getState,adminPageUserDto.getState())
                .like(StringUtils.isNotBlank(adminPageUserDto.getAccount()), User::getAccount, adminPageUserDto.getAccount())
                .like(StringUtils.isNotBlank(adminPageUserDto.getName()), User::getName, adminPageUserDto.getName());
        // 构造查询排序（默认按照创建时间升序排序）
        userLambdaQueryWrapper.orderByAsc(User::getCreateTime);

        // 分页查询
        userMapper.selectPage(page, userLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<AdminPageUserVo> newRecords = page.getRecords().stream().map(user -> {
            AdminPageUserVo adminPageUserVo = new AdminPageUserVo();
            BeanUtils.copyProperties(user, adminPageUserVo);
            return adminPageUserVo;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void addUser(AdminAddUserDto adminAddUserDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, adminAddUserDto.getAccount());
        // 禁止添加已存在同名账号
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 插入数据库
        User user = new User();
        user.setAccount(adminAddUserDto.getAccount());
        user.setPassword(adminAddUserDto.getPassword());
        user.setName(adminAddUserDto.getName());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteUser(Long id) {
        User userInDatabase = userMapper.selectById(id);
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 删除用户信息
        int deleteResult = userMapper.deleteById(id);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.logout(id);
        // 如果业务上有需求在删除用户之后删除用户头像...
        //MinioUtils.delete(userInDatabase.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateInfo(AdminUpdateInfoDto adminUpdateInfoDto) {
        User userInDatabase = userMapper.selectById(adminUpdateInfoDto.getId());
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 禁止添加非自身的已存在同名账号
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, adminUpdateInfoDto.getAccount());
        userLambdaQueryWrapper.ne(User::getId, adminUpdateInfoDto.getId());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 数据只有发生更新之后才可以进行数据库操作
        if (Objects.equals(adminUpdateInfoDto.getAccount(), userInDatabase.getAccount()) && Objects.equals(adminUpdateInfoDto.getName(), userInDatabase.getName())) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "信息未发生更改");
        }
        User user = new User()
                .setId(adminUpdateInfoDto.getId())
                .setAccount(adminUpdateInfoDto.getAccount())
                .setName(adminUpdateInfoDto.getName());
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(adminUpdateInfoDto.getId());
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateState(Long id) {
        User userInDatabase = userMapper.selectById(id);
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 根据数据库中用户状态做出更新
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (!Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            userLambdaUpdateWrapper.set(User::getState, Constants.USER_DISABLE_STATE);
        } else {
            userLambdaUpdateWrapper.set(User::getState, Constants.USER_ENABLE_STATE);
        }
        userLambdaUpdateWrapper.eq(User::getId, id);
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(id);
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void resetPassword(AdminResetPasswordDto adminResetPasswordDto) {
        User userInDatabase = userMapper.selectById(adminResetPasswordDto.getId());
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 重置用户密码
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper
                .set(User::getPassword, adminResetPasswordDto.getNewPassword())
                .eq(User::getId, adminResetPasswordDto.getId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(userInDatabase.getId());
    }

    @Override
    public List<AdminExportVo> exportExcelList() {
        List<User> usersInDatabase = userMapper.selectList(null);
        return usersInDatabase.stream().map(user -> {
            AdminExportVo adminExportVo = new AdminExportVo();
            adminExportVo.setId(user.getId());
            adminExportVo.setAccount(user.getAccount());
            adminExportVo.setName(user.getName());
            adminExportVo.setAvatar(user.getAvatar());
            adminExportVo.setRole(StringUtils.equals(user.getRole(), Constants.ROLE_ADMIN) ? "管理员" : "用户");
            adminExportVo.setState(!Objects.equals(user.getState(), Constants.USER_DISABLE_STATE) ? "启用" : "禁用");
            adminExportVo.setCreateTime(user.getCreateTime());
            adminExportVo.setUpdateTime(user.getUpdateTime());
            return adminExportVo;
        }).collect(Collectors.toList());
    }

}
