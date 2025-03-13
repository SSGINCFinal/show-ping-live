package com.ssginc.showpinglive.dto.object;

import lombok.*;

/**
 * 방송 등록 화면에서 이미 등록한 방송 정보를 보내주기 위해 정의한 DTO 클래스
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetStreamRegisterInfoDto {

    private Long streamNo;

    private String streamTitle;

    private String streamDescription;

    private Long productNo;

    private String productName;

    private Long productPrice;

    private Integer productSale;

    private String productImg;

}
