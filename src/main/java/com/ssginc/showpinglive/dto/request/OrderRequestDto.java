package com.ssginc.showpinglive.dto.request;

import com.ssginc.showpinglive.dto.object.OrderItemDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Long memberNo;
    private Long totalPrice;
    private List<OrderItemDto> orderDetails;
}
