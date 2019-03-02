package com.ruubel.massfollow.dao;

import com.ruubel.massfollow.model.FollowingAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingAmountRepository extends JpaRepository<FollowingAmount, Long> {
}
