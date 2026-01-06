package com.ljw.train.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author :ljw
 * @date : 2025/12/25
 * description :
 */
@Data
public class MemberRegisterReq {

    @NotBlank(message = "【手机号】不能为空")
    private String mobile;

}
