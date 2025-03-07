package com.ssginc.showpinglive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String memberId;
    private String memberName;
    private String memberEmail;
    private String memberPassword;
    private String memberAddress;
    private String memberPhone;

}
