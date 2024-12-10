package org.onewayticket.repository;

import org.onewayticket.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByReferenceCode(String referenceCode);

    List<Booking> findAllByMemberId(Long id);
}
