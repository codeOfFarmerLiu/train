package com.ljw.train.member.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :ljw
 * @date : 2025/12/22
 * description :
 */
@RestController
public class testController {

    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
