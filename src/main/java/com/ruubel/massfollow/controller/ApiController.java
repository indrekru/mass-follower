package com.ruubel.massfollow.controller;

import com.ruubel.massfollow.job.FollowJob;
import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.model.FollowingAmount;
import com.ruubel.massfollow.service.FollowPersistenceService;
import com.ruubel.massfollow.service.FollowingAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private FollowPersistenceService followPersistenceService;
    private FollowingAmountService followingAmountService;
    private FollowJob followJob;

    @Autowired
    public ApiController(FollowPersistenceService followPersistenceService, FollowingAmountService followingAmountService, FollowJob followJob) {
        this.followPersistenceService = followPersistenceService;
        this.followingAmountService = followingAmountService;
        this.followJob = followJob;
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
        List<FollowingAmount> followingAmounts = followingAmountService.findAll();
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
}
