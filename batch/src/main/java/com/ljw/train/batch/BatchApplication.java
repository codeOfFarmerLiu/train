package com.ljw.train.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author :ljw
 * @date : 2026/1/12
 * description :
 */
@SpringBootApplication
@ComponentScan("com.ljw")
@MapperScan("com.ljw.train.*.mapper")
@EnableFeignClients("com.ljw.train.batch.feign")
public class BatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}
