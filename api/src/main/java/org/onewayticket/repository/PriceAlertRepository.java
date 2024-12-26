package org.onewayticket.repository;

import org.onewayticket.domain.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByOriginAndDestinationAndTargetPriceGreaterThanEqual(String origin, String destination, BigDecimal targetAmount);
}
