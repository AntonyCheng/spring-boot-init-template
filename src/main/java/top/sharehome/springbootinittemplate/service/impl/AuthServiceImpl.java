package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeTransactionException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.service.AuthService;

import javax.annotation.Resource;

/**
 * 用户服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void register(AuthRegisterDto authRegisterDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, authRegisterDto.getAccount());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User();
        user.setAccount(authRegisterDto.getAccount());
        user.setPassword(authRegisterDto.getPassword());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public AuthLoginVo login(AuthLoginDto authLoginDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, authLoginDto.getAccount())
                .eq(User::getPassword, authLoginDto.getPassword());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        if (userInDatabase == null) {
            throw new CustomizeReturnException(ReturnCode.WRONG_USER_ACCOUNT_OR_PASSWORD);
        }
        AuthLoginVo authLoginVo = new AuthLoginVo();
        BeanUtils.copyProperties(userInDatabase, authLoginVo);
        return authLoginVo;
    }

}