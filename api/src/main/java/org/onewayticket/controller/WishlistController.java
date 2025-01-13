package org.onewayticket.controller;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> getMyWishlist(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        List<WishlistDto> wishlist = WishlistDto.fromList(wishlistService.getWishlists(username));
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/{flightId}")
    public ResponseEntity<?> addToWishlist(
            HttpServletRequest request,
            @PathVariable @Valid String flightId) {
        String username = (String) request.getAttribute("username");
        wishlistService.addToWishlist(username, flightId);
        return ResponseEntity.status(HttpStatus.CREATED).body("찜 목록에 추가되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(
            HttpServletRequest request,
            @PathVariable Long id) {
        String username = (String) request.getAttribute("username");
        wishlistService.removeFromWishlist(username, id);
        return ResponseEntity.ok("찜 목록에서 제거되었습니다.");
    }

}
