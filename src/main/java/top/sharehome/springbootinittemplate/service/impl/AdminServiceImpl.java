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
                .like(StringUtils.isNotBlank(adminPageUserDto.getAccount()), User::getAccount, adminPageUserDto.getAccount())
                .like(StringUtils.isNotBlank(adminPageUserDto.getName()), User::getName, adminPageUserDto.getName());
        // 构造查询排序（默认按照创建时间升序排序）
        userLambdaQueryWrapper.orderByAsc(User::getCreateTime);

        // 分页查询
        userMapper.selectPage(page, userLambdaQueryWrapper);

        // 处理分页（Entity ==> Vo）
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
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
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
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "无法对管理员进行操作");
        }
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
    public void resetPassword(AdminResetPasswordDto adminResetPasswordDto) {
        User userInDatabase = userMapper.selectById(adminResetPasswordDto.getId());
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "无法对管理员进行操作");
        }
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper
                .set(User::getPassword, adminResetPasswordDto.getNewPassword())
                .eq(User::getId, adminResetPasswordDto.getId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.logout(userInDatabase.getId());
    }

    @Override
    public List<AdminExportVo> exportExcelList() {
        List<User> usersInDatabase = userMapper.selectList(null);
        return usersInDatabase.stream().map(user -> {
            AdminExportVo adminExportVo = new AdminExportVo();
            BeanUtils.copyProperties(user, adminExportVo);
            return adminExportVo;
        }).collect(Collectors.toList());
    }

}
