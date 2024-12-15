package org.onewayticket.repository;

import org.onewayticket.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<List<Wishlist>> findAllByMemberId(Long memberId);

}
