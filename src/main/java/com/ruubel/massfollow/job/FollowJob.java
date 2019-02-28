package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.UnfollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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

//    @Scheduled(cron = "0 55 23 * * ?") // 23:59
    @Scheduled(cron = "0 0 0/6 * * ?") // Every 3 hours
    public void execute() {
        // Initial state
        doLogic(0);
        log.info("Finished, done for today");
    }

    private void doLogic(int unfollowTimes) {
        int currentlyFollowing = followService.getCurrentlyFollowing();
        if (currentlyFollowing < 3500) {
            log.info("Do following first");
            doFollows();
            if (unfollowTimes == 0) {
                // Hasn't unfollowed yet
                log.info("Doing unfollows, haven't done them yet");
                doUnfollows();
            }
        } else {
            log.info("Do unfollowing first");
            if (unfollowTimes > 0) {
                log.info("Have done unfollows already, quit");
                return;
            }
            doUnfollows();
            doLogic(unfollowTimes + 1);
        }
    }

    private void doFollows() {
        List<String> accounts = new ArrayList<String>(){{
            add("airbnb");
            add("santanderuk");
            add("HSBC");
            add("AskLloydsBank");
            add("WesternUnion");
            add("BarackObama");
            add("TheEllenShow");
            add("realDonaldTrump");
            add("BillGates");
            add("elonmusk");
            add("nytimes");
            add("NASA");
            add("ReformedBroker");
            add("TheStalwart");
            add("ritholtz");
            add("StockCats");
            add("awealthofcs");
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
