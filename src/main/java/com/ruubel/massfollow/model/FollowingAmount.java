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

    @Column(name = "me_following")
    private Long imFollowing;

    @Column(name = "following")
    private Long myFollowers;

    @Column(name = "created")
    private Instant created;

    public FollowingAmount() {
    }

    public FollowingAmount(Long imFollowing, Long myFollowers) {
        this.imFollowing = imFollowing;
        this.myFollowers = myFollowers;
        this.created = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getImFollowing() {
        return imFollowing;
    }

    public Long getMyFollowers() {
        return myFollowers;
    }

    public Instant getCreated() {
        return created;
    }
}
