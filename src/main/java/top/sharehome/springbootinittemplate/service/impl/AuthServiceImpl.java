package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.service.AuthService;

import java.util.Objects;

/**
 * 鉴权认证服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Override
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
    public AuthLoginVo login(AuthLoginDto authLoginDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, authLoginDto.getAccount());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_BANNED);
        }
        // todo 思考一下这个地方怎么做到限时登录+封禁这样的逻辑
        if (!Objects.equals(userInDatabase.getPassword(), authLoginDto.getPassword())) {
            LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            if (userInDatabase.getLoginNum() < 5) {
                userLambdaUpdateWrapper
                        .set(User::getLoginNum, userInDatabase.getLoginNum() + 1)
                        .eq(User::getAccount, userInDatabase.getAccount());
                int updateResult = userMapper.update(userLambdaUpdateWrapper);
                if (updateResult == 0) {
                    throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
                }
                throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED, "第" + (userInDatabase.getLoginNum() + 1) + "次错误");
            } else {
                userLambdaUpdateWrapper
                        .set(User::getState, Constants.USER_DISABLE_STATE)
                        .set(User::getLoginNum, 0)
                        .eq(User::getAccount, userInDatabase.getAccount());
                int updateResult = userMapper.update(userLambdaUpdateWrapper);
                if (updateResult == 0) {
                    throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
                }
                throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED, "多次出错，账号已被封禁");
            }
        }
        AuthLoginVo authLoginVo = new AuthLoginVo();
        BeanUtils.copyProperties(userInDatabase, authLoginVo);
        return authLoginVo;
    }

}