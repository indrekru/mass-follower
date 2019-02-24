package com.ruubel.massfollow.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "followed")
public class Followed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "external_name")
    private String externalName;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "followed")
    private Instant followed;

    public Followed() {
    }

    public Followed(String externalName, String externalId) {
        this.externalName = externalName;
        this.externalId = externalId;
        this.followed = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getExternalName() {
        return externalName;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getFollowed() {
        return followed;
    }
}
