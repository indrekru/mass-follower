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
        log.info("Cleanup of old stats, leave last of the day");

        Instant then = Instant.now().minusSeconds(2592000); // 30 days ago
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedLessThanOrderByCreatedAsc(then);
        for (int i = 0; i < followingAmounts.size(); i++){
            FollowingAmount current = followingAmounts.get(i);
            FollowingAmount next;
            if (followingAmounts.size() > i + 1) {
                next = followingAmounts.get(i + 1);
                String currentDate = formatter.format(current.getCreated());
                String nextDate = formatter.format(next.getCreated());
                if (currentDate.equals(nextDate)) {
                    followingAmountService.delete(current);
                }
            }
        }
    }

}
