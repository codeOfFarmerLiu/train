package com.ljw.train.business.controller;

import com.ljw.train.business.req.DailyTrainTicketQueryReq;
import com.ljw.train.business.resp.DailyTrainTicketQueryResp;
import com.ljw.train.business.service.DailyTrainTicketService;
import com.ljw.train.common.resp.CommonResp;
import com.ljw.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :ljw
 * @date : 2026/2/22
 * description :
 */
@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }
}
