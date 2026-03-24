package com.ljw.train.member.req;

import com.ljw.train.common.req.PageReq;
import lombok.Data;

@Data
public class TicketQueryReq extends PageReq {
    private Long memberId;
}
