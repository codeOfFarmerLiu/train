package com.ljw.train.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author :ljw
 * @date : 2025/12/24
 * description :
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ljw"})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
