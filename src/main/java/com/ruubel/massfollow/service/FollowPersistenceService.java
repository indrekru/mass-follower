package com.ruubel.massfollow.service;

import com.ruubel.massfollow.dao.FollowedRepository;
import com.ruubel.massfollow.model.Followed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowPersistenceService {

    private FollowedRepository followedRepository;

    @Autowired
    public FollowPersistenceService(FollowedRepository followedRepository) {
        this.followedRepository = followedRepository;
    }

    public Followed findByExternalId(String externalId) {
        return followedRepository.findByExternalId(externalId);
    }

    public void save(Followed followed) {
        followedRepository.save(followed);
    }

    public List<Followed> findAll() {
        return followedRepository.findAll();
    }
}
