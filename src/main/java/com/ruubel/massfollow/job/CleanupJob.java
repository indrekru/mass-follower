package com.ruubel.massfollow.job;

import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.FollowPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowPersistenceService followPersistenceService;

    @Autowired
    public CleanupJob(FollowPersistenceService followPersistenceService) {
        this.followPersistenceService = followPersistenceService;
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public void cleanup() {
        log.info("Starting cleanup");

        Instant then = Instant.now().minusSeconds(90 * 86400); // 3 months ago
        List<Followed> olds = followPersistenceService.findByFollowedLessThan(then);

        for (Followed old : olds) {
            followPersistenceService.delete(old);
        }

        log.info("Done with the cleanup");
    }
}
