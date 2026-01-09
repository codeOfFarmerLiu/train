package com.ljw.train.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * @author :ljw
 * @date : 2026/1/8
 * description :
 */
@SpringBootApplication
@ComponentScan("com.ljw")
@MapperScan("com.ljw.train.*.mapper")
public class BusinessApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
    }
}
