package com.ljw.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author :ljw
 * @date : 2025/12/29
 * description :
 */
@Data
public class MemberSendCodeReq {
    @NotBlank(message = "【手机号】不能为空")
    @Pattern(regexp = "^\\d{11}$",message = "手机号码格式错误")
    private String mobile;
}
