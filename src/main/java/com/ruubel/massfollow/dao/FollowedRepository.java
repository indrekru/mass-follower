package com.ruubel.massfollow.dao;

import com.ruubel.massfollow.model.Followed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowedRepository extends JpaRepository<Followed, Long> {
    Followed findByExternalId(String externalId);
}
