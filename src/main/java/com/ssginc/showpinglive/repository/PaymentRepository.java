package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
