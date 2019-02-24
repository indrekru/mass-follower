package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.UnfollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class FollowJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowService followService;
    private UnfollowService unfollowService;

    @Autowired
    public FollowJob(FollowService followService, UnfollowService unfollowService) {
        this.followService = followService;
        this.unfollowService = unfollowService;
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public void execute() {

        List<String> accounts = new ArrayList<String>(){{
            add("airbnb");
            add("santanderuk");
            add("HSBC");
            add("AskLloydsBank");
            add("monzo");
        }};

        Random random = new Random();
        String account = accounts.get(random.nextInt(accounts.size()));

        log.info(String.format("Start following '%s'", account));
        followService.execute(account);

        log.info("Unfollowing...");
        unfollowService.execute();

        log.info("Finished unfollowing, done for today");
    }
}
