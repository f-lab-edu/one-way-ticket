package org.onewayticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.WishlistDto;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getMyWishlist(@RequestHeader("Authorization") String header) {
        String token = jwtUtil.extractTokenFromHeader(header);
        List<WishlistDto> wishlist = WishlistDto.fromList(wishlistService.getWishlists(token));
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/{flightId}")
    public ResponseEntity<?> addToWishlist(
            @RequestHeader("Authorization") String header,
            @PathVariable @Valid Long flightId) {
        String token = jwtUtil.extractTokenFromHeader(header);
        wishlistService.addToWishlist(token, flightId);
        return ResponseEntity.status(HttpStatus.CREATED).body("찜 목록에 추가되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(
            @RequestHeader("Authorization") String header,
            @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(header);
        wishlistService.removeFromWishlist(token, id);
        return ResponseEntity.ok("찜 목록에서 제거되었습니다.");
    }

}
