package com.imyuanxiao.uls.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imyuanxiao.uls.model.entity.UserLoginHistory;
import com.imyuanxiao.uls.model.entity.UserProfile;
import com.imyuanxiao.uls.model.param.UserParam;
import com.imyuanxiao.uls.model.vo.UserDetailsVO;
import com.imyuanxiao.uls.model.vo.UserPageVO;
import com.imyuanxiao.uls.security.JwtManager;
import com.imyuanxiao.uls.service.*;
import com.imyuanxiao.uls.util.RedisUtil;
import com.imyuanxiao.uls.util.SecurityContextUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imyuanxiao.uls.enums.ResultCode;
import com.imyuanxiao.uls.exception.ApiException;
import com.imyuanxiao.uls.model.entity.User;
import com.imyuanxiao.uls.mapper.UserMapper;
import com.imyuanxiao.uls.model.param.LoginParam;
import com.imyuanxiao.uls.model.param.RegisterParam;
import com.imyuanxiao.uls.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.imyuanxiao.uls.util.RedisConstants.*;

/**
* @author Administrator
* @description 针对表【user(user table)】的数据库操作Service实现
* @createDate 2023-05-26 17:15:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService, UserDetailsService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserLoginHistoryService loginHistoryService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String sendCode(String email) {
        String code = RandomUtil.randomNumbers(4);
        // Save email and code in redis
        redisUtil.saveCode(email, code);
        // TODO send code to email
        return  "Verification code has been sent: " + code;
    }

    @Override
    public String register(RegisterParam param) {
        String email = param.getEmail();
        // Get verification code from redis
        redisUtil.getCode(param.getEmail(), param.getCode());
        // Add new user
        User user = new User()
                .setEmail(param.getEmail())
                .setState(0)
                .setUserPassword(passwordEncoder.encode(param.getPassword()));

        try {
            this.save(user);
            // Add default user role - 2L user
            roleService.insertRolesByUserId(user.getId(), List.of(2L));
            // Delete email and code from redis
            redisUtil.removeCode(user.getEmail());
            return "Register successfully";

        } catch (Exception e) {
            throw new ApiException(ResultCode.FAILED, "Email is already in use.");
        }
    }

    @Override
    public UserVO login(LoginParam loginParam, HttpServletRequest request) {
        // Verify user from database
        User user = this.lambdaQuery()
                .eq(StrUtil.isNotBlank(loginParam.getEmail()), User::getEmail, loginParam.getEmail())
                .one();

        // Throw error if user or password is wrong
        if(user == null || !passwordEncoder.matches(loginParam.getPassword(), user.getUserPassword())){
            throw new ApiException(ResultCode.VALIDATE_FAILED, "Username or password is incorrect！");
        }

        // If state is abnormal
        if(user.getState() != 0){
            throw new ApiException(user.getState() == 1 ? ResultCode.ACCOUNT_STATE_DISABLED : ResultCode.ACCOUNT_STATE_DELETED);
        }

        // Save login time
        loginHistoryService.save(new UserLoginHistory()
                .setUserId(user.getId())
                .setLoginTime(DateUtil.date())
                .setUserAgent(request.getHeader("User-Agent"))
                .setIpAddress(request.getRemoteAddr()));

        // Put user basic info, profile, token, permissions in UserVO object
        return getUserVO(user);
    }

    private UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        // Copy basic info
        BeanUtil.copyProperties(user, userVO);
        // Copy user profile
        UserProfile userProfile = userProfileService.getByUserId(user.getId());
        // Initialize user profile if new user
        if(userProfile == null){
            userProfile =  new UserProfile()
                    .setNickName(user.getEmail())
                    .setAvatar("https://i.328888.xyz/2023/05/15/VZpOIx.png");
            userProfile.setUserId(user.getId());
            userProfileService.save(userProfile);
        }
        BeanUtil.copyProperties(userProfile, userVO, "id", "userID");
        // Set roleIds and permissionIds
        userVO.setRoleIds(roleService.getIdsByUserId(user.getId()))
                .setPermissionIds(permissionService.getIdsByUserId(user.getId()));
        // Generate token
        String token = JwtManager.generate(user.getEmail());
        userVO.setToken(token);
        // Manually handle or use util to convert id 'long' to 'string'.
        Map<String, Object> userMap = BeanUtil.beanToMap(userVO, new HashMap<>(),
                CopyOptions.create().
                        setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : null));
        // Save user info and token in redis
        redisUtil.saveUserMap(userMap);
        return userVO;
    }

    @Override
    public void logout(HttpServletRequest request) {
        loginHistoryService.save(new UserLoginHistory()
                .setUserId(SecurityContextUtil.getCurrentUserDetailsVO().getUser().getId())
                .setLogoutTime(DateUtil.date())
                .setUserAgent(request.getHeader("User-Agent"))
                .setIpAddress(request.getRemoteAddr()));
        redisUtil.removeUserMap();
    }

    @Override
    public Set<Long> myPermission() {
        Long currentUserId = SecurityContextUtil.getCurrentUserId();
        return permissionService.getIdsByUserId(currentUserId);
    }

    @Override
    public String updateToken() {
        return redisUtil.refreshToken();
    }

    @Override
    public void createUser(UserParam param) {
        if (lambdaQuery().eq(User::getEmail, param.getEmail()).one() != null) {
            throw new ApiException(ResultCode.FAILED,"Username already exists.");
        }
        User user = new User();
        user.setEmail(param.getEmail()).setUserPassword(passwordEncoder.encode(param.getEmail()));
        save(user);
        if (CollectionUtil.isEmpty(param.getRoleIds())) {
            return;
        }
        // Add info in table [user-role]
        roleService.insertRolesByUserId(user.getId(), param.getRoleIds());
    }

    @Override
    public boolean removeByIds(Collection<?> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return false;
        }
        // Delete info from table [user-role]
        for (Object userId : idList) {
            roleService.removeByUserId((Long)userId);
        }
        // Delete user
        baseMapper.deleteBatchIds(idList);
        return true;
    }

    @Override
    public void update(UserParam param) {
        updateRoles(param);
    }

    private void updateRoles(UserParam param) {
        // Delete the original user role
        roleService.removeByUserId(param.getId());
        // If roleIds is empty, delete all roles for this user
        if (CollectionUtil.isEmpty(param.getRoleIds())) {
            return;
        }
        // If roleIds not empty, add new roles for this user
        roleService.insertRolesByUserId(param.getId(), param.getRoleIds());
    }

    @Override
    public User getUserByUsername(String email) {
        if(StrUtil.isBlank(email)){
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        return this.lambdaQuery().eq(User::getEmail, email).one();
    }

    @Override
    public UserDetailsVO loadUserByUsername(String username) throws UsernameNotFoundException {
        // Get user by username
        User user = this.getUserByUsername(username);
        // Get permissionIds and tranfer them to `SimpleGrantedAuthority` Object
        Set<SimpleGrantedAuthority> authorities = permissionService.getIdsByUserId(user.getId())
                .stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new UserDetailsVO(user, authorities);
    }

    @Override
    public IPage<UserPageVO> selectPage(Page<UserPageVO> page) {
        QueryWrapper<UserPageVO> queryWrapper = new QueryWrapper<>();
        // Don't show super admin (id:1) and current user
        Long myId = SecurityContextUtil.getCurrentUserId();
        queryWrapper.ne("id", myId).ne("id", 1);
        // Get page info
        IPage<UserPageVO> pages = baseMapper.selectPage(page, queryWrapper);
        // Get rolse for all users
        for (UserPageVO vo : pages.getRecords()) {
            vo.setRoleIds(roleService.getIdsByUserId(vo.getId()));
//            vo.setCompanyIds(companyService.getIdsByUserId(vo.getId()));
        }
        return pages;
    }

}




