package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.domain.PriceAlert;
import org.onewayticket.repository.PriceAlertRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAlertService {
    private final PriceAlertRepository priceAlertRepository;

    public PriceAlert createPriceAlert(String username, String origin, String destination, BigDecimal targetAmount) {
        PriceAlert priceAlert = PriceAlert.builder().username(username).origin(origin)
                .destination(destination).targetAmount(targetAmount).build();
        priceAlertRepository.save(priceAlert);
        return priceAlert;
    }

    public PriceAlert removePriceAlert(String username, Long priceAlertId) {
        PriceAlert priceAlert = priceAlertRepository.findById(priceAlertId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        if (!username.equals(priceAlert.getUsername())) {
            throw new IllegalArgumentException("로그인한 사용자와 해당 데이터의 사용자가 일치하지 않습니다.");
        }
        priceAlertRepository.deleteById(priceAlertId);
        return priceAlert;
    }

}
