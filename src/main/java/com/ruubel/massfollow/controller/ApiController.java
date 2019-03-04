package com.ruubel.massfollow.controller;

import com.ruubel.massfollow.job.FollowJob;
import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.model.FollowingAmount;
import com.ruubel.massfollow.service.FollowPersistenceService;
import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.FollowingAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private FollowPersistenceService followPersistenceService;
    private FollowingAmountService followingAmountService;
    private FollowJob followJob;
    private FollowService followService;

    @Autowired
    public ApiController(
            FollowPersistenceService followPersistenceService,
            FollowingAmountService followingAmountService,
            FollowJob followJob,
            FollowService followService) {
        this.followPersistenceService = followPersistenceService;
        this.followingAmountService = followingAmountService;
        this.followJob = followJob;
        this.followService = followService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return new ResponseEntity(new HashMap<String, Object>(){{
            put("alive", true);
        }}, HttpStatus.OK);
    }

    @GetMapping("/followed")
    public ResponseEntity followed() {
        List<Followed> followed = followPersistenceService.findAll();
        return new ResponseEntity<>(followed, HttpStatus.OK);
    }

    @GetMapping("/follow-stats")
    public ResponseEntity stats() {
        Instant then = Instant.now().minusSeconds(2592000); // 30 days
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedGreaterThan(then);
        return new ResponseEntity<>(followingAmounts, HttpStatus.OK);
    }

    @PostMapping("/trigger")
    public ResponseEntity trigger() {
        boolean running = followJob.runAsync();
        if (running) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/job-status")
    public ResponseEntity jobStatus() {
        boolean running = followJob.isRunning();
        return new ResponseEntity(new HashMap<String, Object>(){{
            put("running", running);
        }}, HttpStatus.OK);
    }

    @PostMapping("/update-followers")
    public ResponseEntity updateFollowers() {
        Instant past = Instant.now().minusSeconds(3600); // 1 hours
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedGreaterThan(past);
        if (followingAmounts.size() == 0) {
            followJob.updateFollowers();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
