package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.FollowingAmountService;
import com.ruubel.massfollow.service.UnfollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FollowJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowService followService;
    private UnfollowService unfollowService;
    private FollowingAmountService followingAmountService;
    private TaskExecutor taskExecutor;

    private boolean running;

    @Autowired
    public FollowJob(FollowService followService, UnfollowService unfollowService, FollowingAmountService followingAmountService, TaskExecutor taskExecutor) {
        this.followService = followService;
        this.unfollowService = unfollowService;
        this.followingAmountService = followingAmountService;
        this.taskExecutor = taskExecutor;
        this.running = false;
    }

//    @Scheduled(cron = "0 55 23 * * ?") // 23:59
    @Scheduled(cron = "0 0 0/6 * * ?") // Every 6 hours
    public void execute() {
        if (running) {
            return;
        }
        running = true;
        // Initial state
        doLogic(0);
        // Update imFollowing and myFollowers
        updateMyCurrentFollowers();
        log.info("Finished, done for today");
        running = false;
    }

    public boolean runAsync() {
        if (running) {
            return false;
        }
        running = true;
        taskExecutor.execute(() -> {
            doLogic(0);
            running = false;
        });
        return true;
    }

    public void updateFollowers() {
        taskExecutor.execute(() -> updateMyCurrentFollowers());
    }

    public boolean isRunning() {
        return running;
    }

    private void updateMyCurrentFollowers() {
        long[] followers = followService.getImFollowingAndMyFollowers();
        followingAmountService.saveFollowingAmounts(followers[0], followers[1]);
    }

    private void doLogic(int unfollowTimes) {
        long[] followers = followService.getImFollowingAndMyFollowers();
        if (followers[0] < 3500) {
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
