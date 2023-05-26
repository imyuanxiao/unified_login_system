package com.imyuanxiao.unifiedloginsystem.util;

import com.imyuanxiao.unifiedloginsystem.model.entity.User;
import com.imyuanxiao.unifiedloginsystem.model.vo.UserDetailsVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @ClassName SecurityContextUtil
 * @Description TODO
 * @Author imyuanxiao
 * @Date 2023/5/7 12:44
 * @Version 1.0
 **/
public class SecurityContextUtil {

    /**
     * Get user ID from spring security context
     * @author imyuanxiao
     * @date 14:52 2023/5/7
     * @return User ID
     **/
    public static Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsVO userDetails = (UserDetailsVO)authentication.getPrincipal();
        return userDetails.getId();
    }

    /**
     * Get user object from spring security context
     * @author imyuanxiao
     * @date 12:14 2023/5/9
     * @return User Object
     **/
    public static User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsVO userDetails = (UserDetailsVO)authentication.getPrincipal();
        return userDetails.getUser();
    }

}
