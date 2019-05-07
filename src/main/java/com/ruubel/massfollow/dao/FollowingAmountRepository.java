package com.ruubel.massfollow.dao;

import com.ruubel.massfollow.model.FollowingAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FollowingAmountRepository extends JpaRepository<FollowingAmount, Long> {
    List<FollowingAmount> findByCreatedGreaterThanOrderByCreatedAsc(Instant then);
    List<FollowingAmount> findByCreatedLessThanOrderByCreatedAsc(Instant then);
}
