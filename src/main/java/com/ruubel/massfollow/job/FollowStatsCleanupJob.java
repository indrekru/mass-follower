package com.ruubel.massfollow.job;

import com.ruubel.massfollow.model.FollowingAmount;
import com.ruubel.massfollow.service.FollowingAmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Component
public class FollowStatsCleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowingAmountService followingAmountService;
    private DateTimeFormatter formatter;

    @Autowired
    public FollowStatsCleanupJob(FollowingAmountService followingAmountService) {
        this.followingAmountService = followingAmountService;
        formatter = DateTimeFormatter.ofPattern("dd-MM")
            .withLocale( Locale.UK )
            .withZone( ZoneId.systemDefault() );
    }

    @Scheduled(cron = "0 55 23 * * ?") // 23:55
    public void cleanup() {
        log.info("Clean up older than 30 days first");

        Instant then = Instant.now().minusSeconds(2592000); // 30 days ago
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedLessThanOrderByCreatedAsc(then);
        log.info("Found 30 days older: " + followingAmounts.size());
        for (FollowingAmount followingAmount : followingAmounts) {
            followingAmountService.delete(followingAmount);
        }

        followingAmounts = followingAmountService.findByCreatedGreaterThanOrderByCreatedAsc(then);

        log.info("Found newer than 30 days: " + followingAmounts.size());
        for (int i = 0; i < followingAmounts.size(); i++){
            FollowingAmount current = followingAmounts.get(i);
            if (followingAmounts.size() > i + 1) {
                FollowingAmount next = followingAmounts.get(i + 1);
                String currentDate = formatter.format(current.getCreated());
                String nextDate = formatter.format(next.getCreated());
                if (currentDate.equals(nextDate)) {
                    log.info("Deleting: " + currentDate);
                    followingAmountService.delete(current);
                } else {
                    log.info("Skipping: " + currentDate);
                }
            }
        }
    }

}
