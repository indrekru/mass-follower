package com.ruubel.massfollow.service;

import com.ruubel.massfollow.util.RawProfileCard;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.List;

public interface FollowStrategy {
    boolean followList(List<RawProfileCard> rawProfileCards);
    List<RawProfileCard> extractProfileCardsFromHtml(Element body);
    JSONObject getNextAccountFollowersBatchJson(String account, String minPosition);
    Element getAccountFollowersPageHtml(String account);
}