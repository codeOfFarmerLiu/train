package com.ljw.train.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :ljw
 * @date : 2026/1/8
 * description :
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World! Business!";
    }
}
