package top.sharehome.springbootinittemplate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeTransactionException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.user.UserLoginDto;
import top.sharehome.springbootinittemplate.model.dto.user.UserRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.user.UserInfoVo;
import top.sharehome.springbootinittemplate.model.vo.user.UserLoginVo;
import top.sharehome.springbootinittemplate.service.UserService;

import javax.annotation.Resource;

/**
 * 用户服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void register(UserRegisterDto userRegisterDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, userRegisterDto.getAccount());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User();
        user.setAccount(userRegisterDto.getAccount());
        user.setPassword(userRegisterDto.getPassword());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public UserLoginVo login(UserLoginDto userLoginDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, userLoginDto.getAccount())
                .eq(User::getPassword, userLoginDto.getPassword());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        if (userInDatabase == null) {
            throw new CustomizeReturnException(ReturnCode.WRONG_USER_ACCOUNT_OR_PASSWORD);
        }
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(userInDatabase, userLoginVo);
        return userLoginVo;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public UserInfoVo info(Long loginId) {
        User userInDatabase = userMapper.selectById(loginId);
        if (ObjectUtils.isEmpty(userInDatabase)) {
            StpUtil.logout(loginId);
            throw new CustomizeReturnException(ReturnCode.ACCESS_UNAUTHORIZED);
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInDatabase, userInfoVo);
        return userInfoVo;
    }

}