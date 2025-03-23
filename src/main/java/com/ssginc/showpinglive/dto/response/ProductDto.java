package com.ssginc.showpinglive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productNo;
    private String productName;
    private Long productPrice;
    private Long productQuantity;
    private String productImg;
    private String productDescript;
    private Integer productSale;
    private Long discountedPrice;
    private Long reviewCount;
    private Double reviewAverage;
    private Long productSaleQuantity;
}
