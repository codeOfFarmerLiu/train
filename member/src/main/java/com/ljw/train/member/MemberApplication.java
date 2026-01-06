package com.ljw.train.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author :ljw
 * @date : 2025/12/22
 * description :
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ljw"})
@MapperScan("com.ljw.train.member.mapper")
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
