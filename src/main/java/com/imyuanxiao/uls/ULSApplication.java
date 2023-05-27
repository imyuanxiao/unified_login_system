package com.imyuanxiao.uls;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
@MapperScan(basePackages = {"com.imyuanxiao.uls.mapper"})
public class ULSApplication {

    public static void main(String[] args) {
        SpringApplication.run(ULSApplication.class, args);
    }

}
