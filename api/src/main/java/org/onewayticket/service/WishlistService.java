package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Wishlist;
import org.onewayticket.repository.WishlistRepository;
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

    public List<Wishlist> getWishlists(String username) {
        Member member = memberService.getMemberByUsername(username);
        return wishlistRepository.findAllByMemberId(member.getId()).orElseThrow();
    }

    public Wishlist addToWishlist(String username, Long flightId) {
        Member member = memberService.getMemberByUsername(username);
        Wishlist wishlist = new Wishlist(member.getId(), flightId);
        try {
            wishlistRepository.save(wishlist);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("wishlist 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
        return wishlist;
    }

    public void removeFromWishlist(String username, Long id) {
        Member member = memberService.getMemberByUsername(username);
        Wishlist wishlist = wishlistRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 찜 목록을 찾을 수 없습니다."));
        if (wishlist.getMemberId() == member.getId()) {
            try {
                wishlistRepository.delete(wishlist);
            } catch (DataAccessException e) {
                throw new DataIntegrityViolationException("wishlist 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
            }
        }
    }
}
