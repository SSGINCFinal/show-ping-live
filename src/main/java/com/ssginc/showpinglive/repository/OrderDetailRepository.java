package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.OrderDetail;
import com.ssginc.showpinglive.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
