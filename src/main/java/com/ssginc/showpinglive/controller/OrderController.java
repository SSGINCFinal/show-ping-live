package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.entity.Orders;
import com.ssginc.showpinglive.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 특정 회원의 가장 최근 주문 내역 조회
    @GetMapping("/latest/{memberNo}")
    public ResponseEntity<Orders> getLatestOrder(@PathVariable Long memberNo) {
        Optional<Orders> latestOrder = orderService.findLatestOrderByMemberNo(memberNo);

        return latestOrder
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build()); // 204 No Content 반환
    }
}
