package com.imyuanxiao.uls.controller.api;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.imyuanxiao.uls.model.param.LoginParam;
import com.imyuanxiao.uls.model.param.RegisterParam;
import com.imyuanxiao.uls.model.vo.UserVO;
import com.imyuanxiao.uls.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @ClassName AuthController
 * @Description 登录注册接口
 * @Author imyuanxiao
 * @Date 2023/5/3 16:29
 * @Version 1.0
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@Api(tags = "Auth Management Interface")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
    * @description: show code in frontend (actually should be void)
    * @param emailJsonString email address
    * @return java.lang.String
    * @author imyuanxiao
    * @date 2023/05/26 22:37
    */
    @PostMapping("/code")
    @ApiOperation(value = "Get Verification Code")
    public String sendCode(@RequestBody @NotBlank String emailJsonString){
        JSONObject jsonObject = JSONUtil.parseObj(emailJsonString);
        return userService.sendCode(jsonObject.getStr("email"));
    }

    /**
    * @description: register
    * @param param register-related parameters
    * @return com.imyuanxiao.uls.model.vo.UserVO
    * @author imyuanxiao
    * @date 2023/05/26 23:29
    */
    @PostMapping("/register")
    @ApiOperation(value = "Register using email")
    public String register(@RequestBody RegisterParam param){
        return userService.register(param);
    }

    /**
    * @description: login
    * @param param login-related parameters
    * @return com.imyuanxiao.uls.model.vo.UserVO
    * @author imyuanxiao
    * @date 2023/05/26 23:29
    */
    @PostMapping("/login")
    @ApiOperation(value = "Login using password")
    public UserVO login(@RequestBody  @Valid LoginParam param, HttpServletRequest request){
        return userService.login(param, request);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "Logout")
    public void logout(HttpServletRequest request){ userService.logout(request);}

    @GetMapping("/update-token")
    @ApiOperation(value = "Update token")
    public String updateToken(){
        return userService.updateToken();
    }
    @GetMapping("/my-permission")
    @ApiOperation(value = "Get UserVO every time route changes")
    public Set<Long> myPermission(){
        return userService.myPermission();
    }

}
