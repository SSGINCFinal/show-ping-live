package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.OrderDetail;
import com.ssginc.showpinglive.entity.OrderDetailId;
import com.ssginc.showpinglive.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {

    // 특정 주문의 상세 목록 조회
    List<OrderDetail> findByOrder(Orders order);
}
