package org.onewayticket.dto;

import org.onewayticket.domain.Wishlist;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistDto(
        Long flightId,
        LocalDateTime createdAt
) {
    public static WishlistDto from(Wishlist wishlist) {
        return new WishlistDto(wishlist.getFlightId(), wishlist.getCreatedAt());
    }

    public static List<WishlistDto> fromList(List<Wishlist> wishlists) {
        return wishlists.stream()
                .map(wishlist -> WishlistDto.from(wishlist))
                .toList();
    }

}
