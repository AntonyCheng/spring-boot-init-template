package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.user.UserLoginDto;
import top.sharehome.springbootinittemplate.model.dto.user.UserRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.user.UserInfoVo;
import top.sharehome.springbootinittemplate.model.vo.user.UserLoginVo;

/**
 * 用户服务类
 *
 * @author AntonyCheng
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册Dto类
     */
    void register(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     *
     * @param userLoginDto 用户登录Dto类
     * @return 返回登录用户信息
     */
    UserLoginVo login(UserLoginDto userLoginDto);

    /**
     * 获取登录用户信息
     *
     * @return 返回用户信息结果
     */
    UserInfoVo info(Long loginId);

}