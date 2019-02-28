package com.ruubel.massfollow.service;

import com.ruubel.massfollow.config.ConfigParams;
import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.http.HttpRequestService;
import com.ruubel.massfollow.util.RawProfileCard;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnfollowService extends AbstractFollowService {

    private FollowPersistenceService followPersistenceService;
    private ConfigParams configParams;

    private double waitBetweenUnfollowsSeconds = 0.1;
    private double waitBetweenNextPageFetchSeconds = 0.1;

    @Autowired
    public UnfollowService(
            FollowPersistenceService followPersistenceService,
            HeaderService headerService,
            HttpRequestService httpRequestService,
            ConfigParams configParams) {
        super(headerService, httpRequestService);
        this.followPersistenceService = followPersistenceService;
        this.configParams = configParams;
    }

    public void execute() {
        execute(configParams.getHomeAccount(), waitBetweenNextPageFetchSeconds);
    }

    public JSONObject getNextAccountFollowersBatchJson(String account, String minPosition) {
        HttpHeaders headers = headerService.getNextHomeFollowerBatchJsonHeaders();
        return requestJson(
                headers,
                String.format("https://twitter.com/%s/following/users?include_available_features=1&include_entities=1&max_position=%s&reset_error_state=false", account, minPosition)
        );
    }

    public boolean followList(List<RawProfileCard> rawProfileCards) {
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
        } else {
            // Nothing in DB, so remove it
            return true;
        }
    }

    private boolean attemptUnfollowAndSleep(String name, String userId) {
        log.info("Trying to unfollow " + name + "(ID:" + userId + ")");
        boolean success = unfollow(userId);
        if (!success) {
            log.warn("Failed unfollow. Abort");
            return false;
        }
        sleep(waitBetweenUnfollowsSeconds);
        return true;
    }

    public List<RawProfileCard> extractProfileCardsFromHtml(Element parent) {
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

    public Element getAccountFollowersPageHtml(String account) {
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
