package com.ljw.train.business.req;

import com.ljw.train.common.req.PageReq;
import lombok.Data;

@Data
public class TrainSeatQueryReq extends PageReq {

    private String trainCode;

}
