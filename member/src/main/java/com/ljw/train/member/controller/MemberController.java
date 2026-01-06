package com.ljw.train.member.controller;

import com.ljw.train.common.resp.CommonResp;
import com.ljw.train.member.req.MemberLoginReq;
import com.ljw.train.member.req.MemberRegisterReq;
import com.ljw.train.member.req.MemberSendCodeReq;
import com.ljw.train.member.resp.MemberLoginResp;
import com.ljw.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * @author :ljw
 * @date : 2025/12/25
 * description :
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count() {
        int count = memberService.count();
        CommonResp<Integer> commonResp = new CommonResp<>();
        commonResp.setContent(count);
        return commonResp;
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req) {
        long register =memberService.register(req);
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setContent(register);
        return commonResp;
    }

    @PostMapping("/sendCode")
    public CommonResp<Long> sendCode(@Valid @RequestBody(required = false) MemberSendCodeReq req){
        memberService.sendCode(req);
        return new CommonResp<>();
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req) {
        MemberLoginResp resp = memberService.login(req);
        return new CommonResp<>(resp);
    }
}
