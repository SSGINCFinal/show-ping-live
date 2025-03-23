package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.request.OrderRequestDto;
import com.ssginc.showpinglive.dto.response.OrderDetailDto;
import com.ssginc.showpinglive.dto.response.OrdersDto;
import com.ssginc.showpinglive.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping("/member/{memberNo}")
    public ResponseEntity<List<OrdersDto>> getAllOrdersByMember(@PathVariable Long memberNo) {
        List<OrdersDto> orders = orderService.findAllOrdersByMember(memberNo);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderNo}/details")
    public ResponseEntity<List<OrderDetailDto>> getOrderDetails(@PathVariable Long orderNo) {
        List<OrderDetailDto> orderDetails = orderService.findOrderDetailsByOrder(orderNo);
        return ResponseEntity.ok(orderDetails);
    }

    // 주문 생성 API
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok("주문이 성공적으로 저장되었습니다.");
    }

}