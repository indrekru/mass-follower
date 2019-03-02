package com.ruubel.massfollow.service;

import com.ruubel.massfollow.dao.FollowingAmountRepository;
import com.ruubel.massfollow.model.FollowingAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingAmountService {

    private FollowingAmountRepository followingAmountRepository;

    @Autowired
    public FollowingAmountService(FollowingAmountRepository followingAmountRepository) {
        this.followingAmountRepository = followingAmountRepository;
    }

    public void saveFollowingAmount(long amount) {
        FollowingAmount followingAmount = new FollowingAmount(amount);
        followingAmountRepository.save(followingAmount);
    }

    public List<FollowingAmount> findAll() {
        return followingAmountRepository.findAll();
    }
}