package com.imyuanxiao.uls.exception;

import org.springframework.security.core.AuthenticationException;
import lombok.Data;

/**
 * @version 1.0
 * @description AccountTakeoverException
 * @author: imyuanxiao
 * @date: 2023/5/27 15:10
 **/
public class AccountTakeoverException extends RuntimeException {
    private String msg;
    public AccountTakeoverException(String msg) {
        super(msg);
    }

}
