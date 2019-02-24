package com.ruubel.massfollow.service;

import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.http.HttpRequestService;
import com.ruubel.massfollow.util.RawProfileCard;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnfollowService extends AbstractFollowService {

    private FollowPersistenceService followPersistenceService;

    private double waitBetweenUnfollowsSeconds = 1;
    private double waitBetweenNextPageFetchSeconds = 0.1;

    private String homeAccount;

    @Autowired
    public UnfollowService(
            FollowPersistenceService followPersistenceService,
            HeaderService headerService,
            HttpRequestService httpRequestService) throws Exception {
        super(headerService, httpRequestService);
        this.followPersistenceService = followPersistenceService;
        homeAccount = Optional.ofNullable(System.getenv("TWITTER_HOME_ACCOUNT_NAME")).orElseThrow(
                () -> new Exception("TWITTER_HOME_ACCOUNT_NAME is not set in the environment"));
    }

    public void execute() {

        Element body = getHomeFollowersPageHtml();

        String minPosition = extractMinPositionFromHtml(body);

        List<RawProfileCard> rawProfileCards = extractProfileCardsFromHtml(body);

        log.info("First batch size: " + rawProfileCards.size());
        log.info(rawProfileCards.toString());

        boolean success = unfollow(rawProfileCards);
        if (!success) {
            return;
        }

        boolean hasNextBatch = true;

        while (minPosition != null && hasNextBatch) {
            log.info("Fetching next batch for " + minPosition);
            JSONObject nextBatchJson = getNextHomeFollowersBatchJson(homeAccount, minPosition);
            if (nextBatchJson == null) {
                // Probably 429 - Too many requests
                return;
            }
            boolean hasMinPosition = nextBatchJson.has("min_position");
            if (hasMinPosition) {
                minPosition = nextBatchJson.getString("min_position");
            } else {
                minPosition = null;
            }

            String itemsHtml = nextBatchJson.getString("items_html");
            body = Jsoup.parse(itemsHtml).body();

            rawProfileCards = extractProfileCardsFromHtml(body);
            log.info("Next batch size: " + rawProfileCards.size());

            success = unfollow(rawProfileCards);
            if (!success) {
                return;
            }

            if (rawProfileCards.size() == 0) {
                hasNextBatch = false;
            }

            try {
                Thread.sleep((long)(waitBetweenNextPageFetchSeconds * 1000.0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    protected JSONObject getNextHomeFollowersBatchJson(String account, String minPosition) {
        HttpHeaders headers = headerService.getNextHomeFollowerBatchJsonHeaders();
        return requestJson(
                headers,
                String.format("https://twitter.com/%s/following/users?include_available_features=1&include_entities=1&max_position=%s&reset_error_state=false", account, minPosition)
        );
    }

    private List<RawProfileCard> extractProfileCardsFromHtml(Element parent) {
        List<RawProfileCard> out = new ArrayList<>();
        Elements profileCards = parent.select("div.ProfileCard.js-actionable-user");
        for (Element profileCard : profileCards) {
            Element actionButtonElement = profileCard.select("div.user-actions.btn-group").get(0);
            boolean following = false;
            if (actionButtonElement.hasClass("following")) {
                following = true;
            }
            if (actionButtonElement.hasClass("pending")) {
                following = true;
            }
            String name = profileCard.attr("data-screen-name");
            String userId = profileCard.attr("data-user-id");
            RawProfileCard rawProfileCardObj = new RawProfileCard(name, userId, following);
            out.add(rawProfileCardObj);
        }
        return out;
    }


    private boolean unfollow(List<RawProfileCard> rawProfileCards) {
        for (RawProfileCard rawProfileCard : rawProfileCards) {
            Followed followed = followPersistenceService.findByExternalId(rawProfileCard.getUserId());
            boolean shouldUnfollow = shouldUnfollow(followed);
            if (shouldUnfollow) {
                boolean success = attemptUnfollowAndSleep(rawProfileCard.getName(), rawProfileCard.getUserId());
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean shouldUnfollow(Followed followed) {
        if (followed != null) {
            Instant followedDate = followed.getFollowed();
            Instant now = Instant.now();
            Duration duration = Duration.between(followedDate, now);
            if (Math.abs(duration.toHours()) >= 48) {
                log.info("Have followed at least 2 days, remove");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean attemptUnfollowAndSleep(String name, String userId) {
        log.info("Trying to unfollow " + name + "(ID:" + userId + ")");
        boolean success = unfollow(userId);
        if (!success) {
            log.warn("Failed unfollow. Abort");
            return false;
        }
        try {
            Thread.sleep((long)(waitBetweenUnfollowsSeconds * 1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Element getHomeFollowersPageHtml() {
        HttpHeaders headers = headerService.getHomeFollowersPageHtmlHeaders();
        return requestElement(
                headers,
                "https://twitter.com/following"
        );
    }

    private boolean unfollow(String userId) {
        return followAction(FollowAction.UNFOLLOW, userId);
    }

}
