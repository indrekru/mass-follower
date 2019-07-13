package com.ruubel.massfollow.job;

import com.ruubel.massfollow.service.FollowService;
import com.ruubel.massfollow.service.FollowingAmountService;
import com.ruubel.massfollow.service.MailingService;
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
    private MailingService mailingService;

    private boolean running;

    @Autowired
    public FollowJob(FollowService followService, UnfollowService unfollowService, FollowingAmountService followingAmountService, MailingService mailingService) {
        this.followService = followService;
        this.unfollowService = unfollowService;
        this.followingAmountService = followingAmountService;
        this.mailingService = mailingService;
        this.running = false;
    }

//    @Scheduled(cron = "0 55 23 * * ?") // 23:59
    @Scheduled(cron = "0 0/10 * * * ?") // Every 10 minutes
    public void execute() {
        if (running) {
            return;
        }
        running = true;
        // Initial state
        doLogic(0);
        // Update imFollowing and myFollowers
        long[] followers = followService.getImFollowingAndMyFollowers();
        long imFollowing = followers[0];
        long myFollowers = followers[1];
        if (imFollowing == 0) {
            log.info("Account blocked with recaptcha, notify");
            mailingService.notifyRecaptchaBlock();
        }
        updateMyCurrentFollowers(imFollowing, myFollowers);
        log.info("Finished, done for today");
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void updateMyCurrentFollowers(long imFollowing, long myFollowers) {
        followingAmountService.saveFollowingAmounts(imFollowing, myFollowers);
    }

    private void doLogic(int unfollowTimes) {
        long[] followers = followService.getImFollowingAndMyFollowers();
        if (followers[0] < 3500) {
            log.info("Do following");
            doFollows();
            // Disabled cause the follows get very low otherwise
//            if (unfollowTimes == 0) {
//                doUnfollows();
//            }
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
            add("John_Hempton");
            add("BarbarianCap");
            add("muddywatersre");
            add("Carl_C_Icahn");
            add("herbgreenberg");
            add("zerohedge");
            add("pmarca");
            add("WarrenBuffett");
            add("LendingClub");
            add("Zopa");
            add("RobinhoodApp");
            add("RevolutApp");
            add("monzo");
            add("imaginecurve");
            add("starlingbank");
            add("freetrade");
            add("IBKR");
            add("degiroeu");
            add("GrantCardone");
            add("theRealKiyosaki");
            add("Citibank");
            add("jpmorgan");
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
