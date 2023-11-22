package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;

/**
 * 用户服务类
 *
 * @author AntonyCheng
 */
public interface AuthService extends IService<User> {

    /**
     * 用户注册
     *
     * @param authRegisterDto 用户注册Dto类
     */
    void register(AuthRegisterDto authRegisterDto);

    /**
     * 用户登录
     *
     * @param authLoginDto 用户登录Dto类
     * @return 返回登录用户信息
     */
    AuthLoginVo login(AuthLoginDto authLoginDto);

}