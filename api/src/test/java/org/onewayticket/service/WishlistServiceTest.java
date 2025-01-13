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
    @DisplayName("사용자는 자신의 찜 목록을 조회할 수 있습니다.")
    void GetWishlist_Success() {
        // Given
        String username = "testUser";
        Member member = new Member(1L, username, "password");

        Mockito.when(memberService.getMemberByUsername(username)).thenReturn(member);
        Mockito.when(wishlistRepository.findAllByMemberId(1L)).thenReturn(Optional.of(List.of(
                new Wishlist(1L, "1001"),
                new Wishlist(1L, "1002")
        )));

        // When
        List<Wishlist> wishlist = wishlistService.getWishlists(username);

        // Then
        assertNotNull(wishlist);
        assertEquals(2, wishlist.size());
    }

    @Test
    @DisplayName("찜 목록에 항공편을 추가할 수 있습니다.")
    void AddToWishlist_Success() {
        // Given
        String username = "testUser";
        String flightId = "1001";
        Member member = new Member(1L, username, "password");

        Mockito.when(memberService.getMemberByUsername(username)).thenReturn(member);
        Mockito.when(wishlistRepository.save(any(Wishlist.class))).thenReturn(new Wishlist(1L, flightId));

        // When
        Wishlist result = wishlistService.addToWishlist(username, flightId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMemberId());
        assertEquals("1001", result.getFlightId());
    }

    @Test
    @DisplayName("찜 목록에서 항공편을 제거할 수 있습니다.")
    void RemoveFromWishlist_Success() {
        // Given
        String username = "testUser";
        Long wishlistId = 1L;
        Member member = new Member(1L, username, "password");
        Wishlist wishlist = new Wishlist(1L, "1001");

        Mockito.when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(wishlist));
        Mockito.when(memberService.getMemberByUsername(username)).thenReturn(member);

        // When
        wishlistService.removeFromWishlist(member.getUsername(), wishlistId);

        // Then
        Mockito.verify(wishlistRepository, times(1)).delete(wishlist);
    }

    @Test
    @DisplayName("존재하지 않은 찜 상품은 찜 목록에서 제거할 수 없습니다.")
    void RemoveFromWishlist_ItemNotFound() {
        // Given
        Long wishlistId = 1L;

        Mockito.when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> wishlistService.removeFromWishlist("test-user", wishlistId));
        assertTrue(exception.getMessage().contains("해당 찜 목록을 찾을 수 없습니다."));
    }
}
