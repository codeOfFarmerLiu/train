package com.ljw.train.business.feign;

import com.ljw.train.common.req.MemberTicketReq;
import com.ljw.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author :ljw
 * @date : 2026/3/24
 * description :
 */
@FeignClient(name = "member", url = "http://127.0.0.1:8001")
public interface MemberFeign {

    @PostMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq req);

}
