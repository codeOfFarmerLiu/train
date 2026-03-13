package com.ljw.train.business.controller.admin;

import com.ljw.train.common.resp.CommonResp;
import com.ljw.train.common.resp.PageResp;
import com.ljw.train.business.req.ConfirmOrderQueryReq;
import com.ljw.train.business.req.ConfirmOrderDoReq;
import com.ljw.train.business.resp.ConfirmOrderQueryResp;
import com.ljw.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.save(req);
        return new CommonResp<>();
    }
}
