package com.imyuanxiao.uls.model.vo;

import com.imyuanxiao.uls.model.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName UserDetailsVO
 * @Description UserDetails object used for spring security
 * @Author imyuanxiao
 * @Date 2023/5/4 22:44
 * @Version 1.0
 **/
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserDetailsVO extends org.springframework.security.core.userdetails.User {

    private User user;

    private String token;

    public UserDetailsVO(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getPhone() + user.getEmail(), user.getUserPassword(), authorities);
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

}
