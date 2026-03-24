package com.ljw.train.member.feign;

import com.ljw.train.common.req.MemberTicketReq;
import com.ljw.train.common.resp.CommonResp;
import com.ljw.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author :ljw
 * @date : 2026/3/24
 * description :
 */
@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) throws Exception {
        ticketService.save(req);
        return new CommonResp<>();
    }

}
