package com.imyuanxiao.unifiedloginsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
@MapperScan(basePackages = {"com.imyuanxiao.unifiedloginsystem.mapper"})
public class UnifiedLoginSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnifiedLoginSystemApplication.class, args);
    }

}
