package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Wishlist;
import org.onewayticket.repository.WishlistRepository;
import org.onewayticket.security.JwtUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public List<Wishlist> getWishlists(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String usernameFromToken = jwtUtil.getUsername(token);
        Member member = memberService.getMemberByUsername(usernameFromToken);
        return wishlistRepository.findAllByMemberId(member.getId()).orElseThrow();
    }

    public Wishlist addToWishlist(String token, Long flightId) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String usernameFromToken = jwtUtil.getUsername(token);
        Member member = memberService.getMemberByUsername(usernameFromToken);
        Wishlist wishlist = new Wishlist(member.getId(), flightId);
        try {
            wishlistRepository.save(wishlist);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("wishlist 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
        return wishlist;
    }

    public void removeFromWishlist(String token, Long id) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String usernameFromToken = jwtUtil.getUsername(token);
        try {
            Wishlist wishlist = wishlistRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 항공권을 찾을 수 없습니다."));
            wishlistRepository.delete(wishlist);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("wishlist 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
