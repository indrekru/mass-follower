package com.ruubel.massfollow.controller;

import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.FollowPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private FollowPersistenceService followPersistenceService;

    @Autowired
    public HealthController(FollowPersistenceService followPersistenceService) {
        this.followPersistenceService = followPersistenceService;
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
}
