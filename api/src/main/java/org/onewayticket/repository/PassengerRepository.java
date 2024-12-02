package org.onewayticket.repository;

import org.onewayticket.domain.Booking;
import org.onewayticket.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
