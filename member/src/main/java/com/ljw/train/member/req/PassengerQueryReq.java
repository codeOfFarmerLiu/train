package com.ljw.train.member.req;

import com.ljw.train.common.req.PageReq;
import lombok.Data;

/**
 * @author :ljw
 * @date : 2025/12/30
 * description :
 */
@Data
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}
