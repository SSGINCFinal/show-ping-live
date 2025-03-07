package com.ssginc.showpinglive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Long orderNo;
    private Long memberNo;
    private LocalDateTime orderDate;
    private Long totalPrice;
    private List<OrderDetailDto> orderItems;
}
