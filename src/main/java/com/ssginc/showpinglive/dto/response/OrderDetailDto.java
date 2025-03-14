package com.ssginc.showpinglive.dto.response;

import com.ssginc.showpinglive.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDetailDto {
    private String productName;
    private Long orderDetailQuantity;
    private Long orderDetailTotalPrice;

    public OrderDetailDto(OrderDetail orderDetail) {
        this.productName = orderDetail.getProduct().getProductName();
        this.orderDetailQuantity = orderDetail.getOrderDetailQuantity();
        this.orderDetailTotalPrice = orderDetail.getOrderDetailTotalPrice();
    }
}
