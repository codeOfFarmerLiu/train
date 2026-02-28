package com.ljw.train.business.controller;

import com.ljw.train.business.resp.StationQueryResp;
import com.ljw.train.business.service.StationService;
import com.ljw.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author :ljw
 * @date : 2026/2/22
 * description :
 */
@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    private StationService stationService;

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }
}
