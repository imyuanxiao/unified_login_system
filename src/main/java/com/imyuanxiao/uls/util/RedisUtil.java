package com.imyuanxiao.uls.util;

import cn.hutool.core.util.RandomUtil;
import com.imyuanxiao.uls.enums.ResultCode;
import com.imyuanxiao.uls.exception.ApiException;
import com.imyuanxiao.uls.model.entity.User;
import com.imyuanxiao.uls.model.vo.UserDetailsVO;
import com.imyuanxiao.uls.security.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.imyuanxiao.uls.util.RedisConstants.*;

/**
 * @version 1.0
 * @description Quick use of redistemplate
 * @author: imyuanxiao
 * @date: 2023/5/27 12:24
 **/
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void getCode(String email, String code){
        String cacheCode = stringRedisTemplate.opsForValue().get(REGISTER_CODE_KEY + email);
        // If no code or code is incorrect
        if(cacheCode == null || !cacheCode.equals(code)){
            throw new ApiException(ResultCode.VALIDATE_FAILED, "Invalid captcha");
        }
    }
    public void removeCode(String email){
        stringRedisTemplate.delete(REGISTER_CODE_KEY + email);
    }

    public void saveCode(String email, String code){
        // Check whether there is code in redis
        String key = REGISTER_CODE_KEY +email;
        String exists = stringRedisTemplate.opsForValue().get(key);
        if(exists != null){
            throw new ApiException(ResultCode.CODE_EXISTS);
        }
        stringRedisTemplate.opsForValue().set(key, code, REGISTER_CODE_TTL, TimeUnit.MINUTES);
    }

    public void saveUserMap(Map<String, Object> userMap){
        String key = LOGIN_USER_KEY + userMap.get("email");
        stringRedisTemplate.opsForHash().putAll(key, userMap);
        // Set token expire time, which is consistent with jwt token expire time
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
    }

    public Map<Object, Object> getUserMap(String username){
        String key = LOGIN_USER_KEY + username;
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void removeUserMap(){
        String email = SecurityContextUtil.getCurrentUserDetailsVO().getUser().getEmail();
        // Delete token from redis
        stringRedisTemplate.delete(LOGIN_USER_KEY + email);
    }

    public String refreshToken(){

        String email = SecurityContextUtil.getCurrentUserDetailsVO().getUser().getEmail();
        // Rename oldKey newKey
        String oldKey = LOGIN_USER_KEY + email;

        String newToken = JwtManager.generate(email);
        String newKey = LOGIN_USER_KEY + email;

        stringRedisTemplate.rename(oldKey, newKey);
        stringRedisTemplate.opsForHash().put(newKey, "token", newToken);

        // Reset expire time
        stringRedisTemplate.expire(newKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return newToken;
    }

}
