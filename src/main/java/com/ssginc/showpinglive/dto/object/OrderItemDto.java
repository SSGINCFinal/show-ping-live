package com.ssginc.showpinglive.dto.object;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productNo;
    private Long quantity;
    private Long totalPrice;
}
