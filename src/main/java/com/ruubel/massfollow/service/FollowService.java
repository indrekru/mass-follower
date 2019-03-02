package com.ruubel.massfollow.service;

import com.ruubel.massfollow.config.ConfigParams;
import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.http.HttpRequestService;
import com.ruubel.massfollow.service.http.HttpResponse;
import com.ruubel.massfollow.util.RawProfileCard;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FollowService extends AbstractFollowService {

    private FollowPersistenceService followPersistenceService;
    private ConfigParams configParams;

    private double waitBetweenFollowsSeconds = 1;
    private double waitBetweenNextPageFetchSeconds = 0.1;

    @Autowired
    public FollowService(
            FollowPersistenceService followPersistenceService,
            HeaderService headerService,
            HttpRequestService httpRequestService,
            ConfigParams configParams) {
        super(headerService, httpRequestService);
        this.followPersistenceService = followPersistenceService;
        this.configParams = configParams;
    }

    public void execute(String account) {
        execute(account, waitBetweenNextPageFetchSeconds);
    }

    public long getCurrentlyFollowing() {
        HttpResponse response = httpRequestService.exchange(
                String.format("https://twitter.com/%s", configParams.getHomeAccount()),
                Connection.Method.GET,
                new HttpHeaders(),
                new HashMap<>());

        Document parsed = Jsoup.parse(response.getBody());
        Elements numberElements = parsed.select("span.ProfileNav-value");
        if (numberElements.size() > 1) {
            Element followingElement = numberElements.get(1);
            String followingCountStr = followingElement.attr("data-count");
            return Long.parseLong(followingCountStr);
        }
        log.warn("Failed to fetch the HTML for following");
        return 0;
    }

    public JSONObject getNextAccountFollowersBatchJson(String account, String minPosition) {
        HttpHeaders headers = headerService.getNextFollowerBatchJsonHeaders(account);
        return requestJson(
                headers,
                String.format("https://twitter.com/%s/followers/users?include_available_features=1&include_entities=1&max_position=%s&reset_error_state=false", account, minPosition)
        );
    }

    public List<RawProfileCard> extractProfileCardsFromHtml(Element parent) {
        List<RawProfileCard> out = new ArrayList<>();
        Elements profileCards = parent.select("div.ProfileCard.js-actionable-user");
        for (Element profileCard : profileCards) {
            Elements bio = profileCard.select("p.ProfileCard-bio");
            if (bio.size() == 0) {
                log.info("No bio, skip");
                continue;
            }
            if (bio.get(0).text().trim().isEmpty()) {
                log.info("No bio, skip");
                continue;
            }
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

    private boolean attemptFollowAndSleep(String name, String userId) {
        log.info(String.format("Trying to follow '%s' (ID: %s)", name, userId));
        boolean success = follow(userId);
        if (!success) {
            log.warn("Failed follow. Abort");
            return false;
        }
        sleep(waitBetweenFollowsSeconds);
        return true;
    }

    public boolean followList(List<RawProfileCard> rawProfileCards) {
        for (RawProfileCard rawProfileCard : rawProfileCards) {
            if (!rawProfileCard.isFollowing()) {
                Followed followed = followPersistenceService.findByExternalId(rawProfileCard.getUserId());
                if (followed != null) {
                    log.info("Have already followed " + followed.getExternalName() + ", skip");
                    continue;
                }
                boolean success = attemptFollowAndSleep(rawProfileCard.getName(), rawProfileCard.getUserId());
                if (!success) {
                    return false;
                } else {
                    followed = new Followed(rawProfileCard.getName(), rawProfileCard.getUserId());
                    followPersistenceService.save(followed);
                }
            }
        }
        return true;
    }

    public Element getAccountFollowersPageHtml(String account) {
        HttpHeaders headers = headerService.getAccountFollowersPageHtmlHeaders(account);
        return requestElement(
                headers,
                String.format("https://twitter.com/%s/followers", account)
        );
    }

    private boolean follow(String userId) {
        return followAction(FollowAction.FOLLOW, userId);
    }

}
