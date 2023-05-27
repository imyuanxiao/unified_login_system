package com.imyuanxiao.uls.model.param;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName UserParam
 * @Description Receive user-related parameters.
 * @Author imyuanxiao
 * @Date 2023/5/7 11:12
 * @Version 1.0
 **/
@Data
public class UserParam {
    @NotNull(message = "UserID is required.", groups = Update.class)
    private Long id;

    @NotBlank(message = "Email is required.", groups = CreateUser.class)
    @Email(message = "Invalid email address.")
    private String email;

    private List<Long> roleIds;

    public interface Update {}

    public interface CreateUser{}
}