package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.UnfollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FollowJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private FollowService followService;
    private UnfollowService unfollowService;

    private static final String UNFOLLOW_TIMES = "unfollowTimes";

    @Autowired
    public FollowJob(FollowService followService, UnfollowService unfollowService) {
        this.followService = followService;
        this.unfollowService = unfollowService;
    }

//    @Scheduled(cron = "0 55 23 * * ?")
    public void execute() {
        // Initial state
        Map<String, Integer> state = new HashMap<String, Integer>(){{
            put(UNFOLLOW_TIMES, 0);
        }};
        doLogic(state);
        log.info("Finished, done for today");
    }

    private void doLogic(Map<String, Integer> state) {
        int currentlyFollowing = followService.getCurrentlyFollowing();
        if (currentlyFollowing < 3500) {
            log.info("Do following first");
            doFollows();
            if (state.get(UNFOLLOW_TIMES) == 0) {
                // Hasn't unfollowed yet
                log.info("Doing unfollows, haven't done them yet");
                doUnfollows();
            }
        } else {
            log.info("Do unfollowing first");
            if (state.get(UNFOLLOW_TIMES) > 0) {
                log.info("Have done unfollows already, quit");
                return;
            }
            doUnfollows();
            state.put(UNFOLLOW_TIMES, state.get(UNFOLLOW_TIMES) + 1);
            doLogic(state);
        }
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
