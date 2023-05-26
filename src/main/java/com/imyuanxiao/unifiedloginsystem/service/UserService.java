package com.imyuanxiao.unifiedloginsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imyuanxiao.unifiedloginsystem.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imyuanxiao.unifiedloginsystem.model.param.LoginParam;
import com.imyuanxiao.unifiedloginsystem.model.param.RegisterParam;
import com.imyuanxiao.unifiedloginsystem.model.param.UserParam;
import com.imyuanxiao.unifiedloginsystem.model.vo.UserPageVO;
import com.imyuanxiao.unifiedloginsystem.model.vo.UserVO;

import java.util.Set;

/**
* @author Administrator
* @description 针对表【user(user table)】的数据库操作Service
* @createDate 2023-05-26 17:15:53
*/
public interface UserService extends IService<User> {

    User getUserByUsername(String username);

    /**
     * Log in
     * @author imyuanxiao
     * @date 11:49 2023/5/7
     * @param loginParam Login form parameters
     * @return If the login is successful, the VO object is returned, and an exception is thrown if it fails
     **/
    UserVO login(LoginParam loginParam);

    /**
     * Register
     * @author imyuanxiao
     * @date 11:49 2023/5/7
     * @param registerParam Register form parameters
     * @return If the register is successful, the VO object is returned, and an exception is thrown if it fails
     **/
    UserVO register(RegisterParam registerParam);

    /**
     * Add new user
     * @author imyuanxiao
     * @date 11:56 2023/5/7
     * @param param user form parameters
     **/
    void createUser(UserParam param);

    /**
     * Update user information
     * @author imyuanxiao
     * @date 11:56 2023/5/7
     * @param param user form parameters
     **/
    void update(UserParam param);

    /**
     * Get pagination information
     * @author imyuanxiao
     * @date 11:55 2023/5/7
     * @param page pagination parameters
     * @return pagination object
     **/
    IPage<UserPageVO> selectPage(Page<UserPageVO> page);

    /**
     * Get UserVO by using user in context
     * @author imyuanxiao
     * @date 12:07 2023/5/9
     * @return UserVO object
     **/
    Set<Long> myPermission();

    String updateToken();

}
