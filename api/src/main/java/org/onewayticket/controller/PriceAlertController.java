package org.onewayticket.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.PriceAlert;
import org.onewayticket.dto.PriceAlertDto;
import org.onewayticket.service.PriceAlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alert/price")
@RequiredArgsConstructor
public class PriceAlertController {
    private final PriceAlertService priceAlertService;

    @PostMapping
    public ResponseEntity<?> subscribePriceAlert(HttpServletRequest request, PriceAlertDto priceAlertDto) {
        String username = (String) request.getAttribute("username");
        PriceAlert priceAlert = priceAlertService.createPriceAlert(username, priceAlertDto.origin(), priceAlertDto.destination(), priceAlertDto.targetAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(priceAlert);
    }

    @DeleteMapping("/{priceAlertId}")
    public ResponseEntity<?> unsuscribePriceAlert(HttpServletRequest request, @PathVariable Long priceAlertId) {
        String username = (String) request.getAttribute("username");
        PriceAlert priceAlert = priceAlertService.removePriceAlert(username, priceAlertId);
        return ResponseEntity.status(HttpStatus.OK).body(priceAlert);
    }
}
