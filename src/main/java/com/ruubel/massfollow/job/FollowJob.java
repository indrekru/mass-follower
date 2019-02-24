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

    public void execute() {

        // 1. Follow a bunch of users until you get a 403 from rate limiting
        String account = "BillGates";
        log.info(String.format("Start following '%s'", account));
        followService.execute(account);

        // 2. Unfollow a bunch of users you are currently following
        log.info("Unfollowing...");
        unfollowService.execute();

        log.info("All done for today");
    }
}
