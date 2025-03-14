package com.ssginc.showpinglive.dto.request;

import com.ssginc.showpinglive.dto.response.OrderItemDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Long memberNo;
    private Long totalPrice;
    private List<OrderItemDto> orderItems;
}
