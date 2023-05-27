package com.imyuanxiao.uls.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imyuanxiao.uls.model.entity.UserLoginHistory;
import com.imyuanxiao.uls.service.UserLoginHistoryService;
import com.imyuanxiao.uls.mapper.UserLoginHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_login_history】的数据库操作Service实现
* @createDate 2023-05-26 17:18:09
*/
@Service
public class UserLoginHistoryServiceImpl extends ServiceImpl<UserLoginHistoryMapper, UserLoginHistory>
    implements UserLoginHistoryService{

}




