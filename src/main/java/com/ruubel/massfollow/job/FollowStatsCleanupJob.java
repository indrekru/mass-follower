package com.ruubel.massfollow.job;

import com.ruubel.massfollow.model.FollowingAmount;
import com.ruubel.massfollow.service.FollowingAmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class FollowStatsCleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowingAmountService followingAmountService;

    @Autowired
    public FollowStatsCleanupJob(FollowingAmountService followingAmountService) {
        this.followingAmountService = followingAmountService;
    }

    @Scheduled(cron = "0 55 23 * * ?") // 23:55
    public void cleanup() {
        log.info("Cleaning up follow stats older than 30 days");
        Instant then = Instant.now().minusSeconds(2592000); // 30 days ago
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedLessThan(then);
        for (FollowingAmount followingAmount : followingAmounts) {
            followingAmountService.delete(followingAmount);
        }
    }

}
