package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.request.OrderRequestDto;
import com.ssginc.showpinglive.dto.response.OrderDetailDto;
import com.ssginc.showpinglive.dto.response.OrdersDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface OrderService {
    List<OrdersDto> findAllOrdersByMember(Long memberNo);

    List<OrderDetailDto> findOrderDetailsByOrder(Long orderNo);

    @Transactional
    void createOrder(OrderRequestDto orderRequestDto);
}
