package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.UnfollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Scheduled(cron = "0 55 23 * * ?")
    public void execute() {
        int currentlyFollowing = followService.getCurrentlyFollowing();
        if (currentlyFollowing < 3500) {
            log.info("Do following first");
            doFollows();
            doUnfollows();
        } else {
            log.info("Do unfollowing first");
            doUnfollows();
            doFollows();
        }
        log.info("Finished, done for today");
    }

    private void doFollows() {
        List<String> accounts = new ArrayList<String>(){{
            add("airbnb");
            add("santanderuk");
            add("HSBC");
            add("AskLloydsBank");
            add("WesternUnion");
        }};

        Random random = new Random();
        String account = accounts.get(random.nextInt(accounts.size()));

        log.info(String.format("Start following '%s'", account));
        followService.execute(account);
    }

    private void doUnfollows() {
        log.info("Start unfollowing");
        unfollowService.execute();
    }

}
