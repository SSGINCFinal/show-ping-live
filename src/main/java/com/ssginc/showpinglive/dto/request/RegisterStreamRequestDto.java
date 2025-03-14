package com.ssginc.showpinglive.dto.request;

import lombok.Getter;

@Getter
public class RegisterStreamRequestDto {

    private Long streamNo;

    private String streamTitle;

    private String streamDescription;

    private Long productNo;

    private Integer productSale;

}
