package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.entity.Orders;
import com.ssginc.showpinglive.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private final OrdersRepository orderRepository;

    public OrderService(OrdersRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 특정 회원의 가장 최근 주문 찾기
    public Optional<Orders> findLatestOrderByMemberNo(Long memberNo) {
        return orderRepository.findTopByMember_MemberNoOrderByOrdersDateDesc(memberNo);
    }
}
