package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.FollowingAmountService;
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
    private FollowingAmountService followingAmountService;

    @Autowired
    public FollowJob(FollowService followService, UnfollowService unfollowService, FollowingAmountService followingAmountService) {
        this.followService = followService;
        this.unfollowService = unfollowService;
        this.followingAmountService = followingAmountService;
    }

//    @Scheduled(cron = "0 55 23 * * ?") // 23:59
    @Scheduled(cron = "0 0 0/6 * * ?") // Every 6 hours
    public void execute() {
        // Track current following
        long currentlyFollowing = followService.getCurrentlyFollowing();
        followingAmountService.saveFollowingAmount(currentlyFollowing);
        // Initial state
        doLogic(0);
        log.info("Finished, done for today");
    }

    private void doLogic(int unfollowTimes) {
        long currentlyFollowing = followService.getCurrentlyFollowing();
        if (currentlyFollowing < 3500) {
            log.info("Do following");
            doFollows();
        } else {
            log.info("Do unfollowing");
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
