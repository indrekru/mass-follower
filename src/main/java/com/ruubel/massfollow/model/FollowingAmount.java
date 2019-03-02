package com.ruubel.massfollow.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "following_amount")
public class FollowingAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "following")
    private Long following;

    @Column(name = "created")
    private Instant created;

    public FollowingAmount() {
    }

    public FollowingAmount(Long following) {
        this.following = following;
        this.created = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getFollowing() {
        return following;
    }

    public Instant getCreated() {
        return created;
    }
}
