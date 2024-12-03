package org.onewayticket.repository;

import org.onewayticket.domain.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<TossPayment, Long> {
}
