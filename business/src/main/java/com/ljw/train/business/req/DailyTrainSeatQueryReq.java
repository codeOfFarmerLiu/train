package com.ljw.train.business.req;

import com.ljw.train.common.req.PageReq;
import lombok.Data;

@Data
public class DailyTrainSeatQueryReq extends PageReq {
    /**
     * 车次编号
     */
    private String trainCode;
}
