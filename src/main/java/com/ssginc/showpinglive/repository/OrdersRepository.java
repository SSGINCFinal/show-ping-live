package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 특정 회원의 가장 최근 주문 찾기
    Optional<Orders> findTopByMember_MemberNoOrderByOrdersDateDesc(Long memberNo);
}