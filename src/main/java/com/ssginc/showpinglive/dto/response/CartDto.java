package com.ssginc.showpinglive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long productNo;
    private String productName;
    private Long productPrice;
    private Long cartProductQuantity;
    private String productImg;
}
