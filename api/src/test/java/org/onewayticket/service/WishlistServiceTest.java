package org.onewayticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Wishlist;
import org.onewayticket.repository.WishlistRepository;
import org.onewayticket.security.JwtUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    @DisplayName("유효한 토큰으로 찜 목록 조회 성공")
    void GetWishlist_Success() {
        // Given
        String token = "valid-token";
        String username = "testUser";
        Member member = new Member(1L, username, "password");

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getUsername(token)).thenReturn(username);
        Mockito.when(memberService.getMemberByUsername(username)).thenReturn(member);
        Mockito.when(wishlistRepository.findAllByMemberId(1L)).thenReturn(Optional.of(List.of(
                new Wishlist(1L, 1001L),
                new Wishlist(1L, 1002L)
        )));

        // When
        List<Wishlist> wishlist = wishlistService.getWishlists(token);

        // Then
        assertNotNull(wishlist);
        assertEquals(2, wishlist.size());
    }

    @Test
    @DisplayName("찜 목록 조회 시 유효하지 않은 토큰 예외 발생")
    void GetWishlist_InvalidToken() {
        // Given
        String token = "invalid-token";

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> wishlistService.getWishlists(token));
        assertTrue(exception.getMessage().contains("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("찜 목록에 항공편 추가 성공")
    void AddToWishlist_Success() {
        // Given
        String token = "valid-token";
        String username = "testUser";
        Long flightId = 1001L;
        Member member = new Member(1L, username, "password");

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getUsername(token)).thenReturn(username);
        Mockito.when(memberService.getMemberByUsername(username)).thenReturn(member);
        Mockito.when(wishlistRepository.save(any(Wishlist.class))).thenReturn(new Wishlist(1L, flightId));

        // When
        Wishlist result = wishlistService.addToWishlist(token, flightId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMemberId());
        assertEquals(1001L, result.getFlightId());
    }

    @Test
    @DisplayName("찜 목록에서 항공편 제거 성공")
    void RemoveFromWishlist_Success() {
        // Given
        String token = "valid-token";
        String username = "testUser";
        Long wishlistId = 1L;
        Member member = new Member(1L, username, "password");
        Wishlist wishlist = new Wishlist(1L, 1001L);

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getUsername(token)).thenReturn(username);
        Mockito.when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(wishlist));

        // When
        wishlistService.removeFromWishlist(token, wishlistId);

        // Then
        Mockito.verify(wishlistRepository, times(1)).delete(wishlist);
    }

    @Test
    @DisplayName("찜 목록에서 항공편 제거 실패 (항목 없음)")
    void RemoveFromWishlist_ItemNotFound() {
        // Given
        String token = "valid-token";
        Long wishlistId = 1L;

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> wishlistService.removeFromWishlist(token, wishlistId));
        assertTrue(exception.getMessage().contains("해당 항공권을 찾을 수 없습니다."));
    }
}
