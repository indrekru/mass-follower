package com.ruubel.massfollow.service

import com.ruubel.massfollow.config.ConfigParams
import com.ruubel.massfollow.service.http.HttpRequestService
import com.ruubel.massfollow.service.http.HttpResponse
import org.jsoup.Connection
import spock.lang.Specification

class FollowServiceSpec extends Specification {

    FollowService service
    FollowPersistenceService followedService
    HttpRequestService httpRequestService
    HeaderService headersService
    ConfigParams configParams

    def setup () {
        followedService = Mock(FollowPersistenceService)
        headersService = Mock(HeaderService)
        httpRequestService = Mock(HttpRequestService)
        configParams = Mock(ConfigParams)
        service = new FollowService(followedService, headersService, httpRequestService, configParams)
    }

    def "when requesting followActionRequestParams, then returns well-defined essential params" () {
        when:
            Map<String, String> map = service.getFollowActionRequestData("3")
        then:
            map.containsKey("challenges_passed")
            map.containsKey("handles_challenges")
            map.containsKey("impression_id")
            map.containsKey("include_blocked_by")
            map.containsKey("include_blocking")
            map.containsKey("include_can_dm")
            map.containsKey("include_followed_by")
            map.containsKey("include_mute_edge")
            map.containsKey("skip_status")
            map.containsKey("user_id")
    }

    def "when requst returns 200, then followAction returns true" () {
        given:
            HttpResponse response = new HttpResponse(200, "body")
        when:
            boolean success = service.followAction(FollowAction.FOLLOW, "123")
        then:
            1 * httpRequestService.exchange(
                    FollowAction.FOLLOW.getActionUrl(),
                    Connection.Method.POST,
                    _,
                    _
            ) >> response
            success
    }

    def "when request returns 403 and fails parsing response, then followAction returns false" () {
        given:
            HttpResponse response = new HttpResponse(403, "body")
        when:
            boolean success = service.followAction(FollowAction.FOLLOW, "123")
        then:
            1 * httpRequestService.exchange(
                    FollowAction.FOLLOW.getActionUrl(),
                    Connection.Method.POST,
                    _,
                    _
            ) >> response
            !success
    }

    def "when request returns 403 and failure json is not 162, then followAction returns false" () {
        given:
            HttpResponse response = new HttpResponse(403, "{\"errors\" : [{\"code\" : 101}]}")
        when:
            boolean success = service.followAction(FollowAction.FOLLOW, "123")
        then:
            1 * httpRequestService.exchange(
                    FollowAction.FOLLOW.getActionUrl(),
                    Connection.Method.POST,
                    _,
                    _
            ) >> response
            !success
    }

    def "when request returns 403 and failure json is 162, then followAction returns true" () {
        given:
            HttpResponse response = new HttpResponse(403, "{\"errors\" : [{\"code\" : 162}]}")
        when:
            boolean success = service.followAction(FollowAction.FOLLOW, "123")
        then:
            1 * httpRequestService.exchange(
                    FollowAction.FOLLOW.getActionUrl(),
                    Connection.Method.POST,
                    _,
                    _
            ) >> response
            success
    }

    def "when fails to connect, then returns 0" () {
        given:
            HttpResponse response = new HttpResponse(500, "exception")
        when:
            int following = service.getCurrentlyFollowing()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == 0
    }

    def "when fetches correct html, but less elements than expected, then returns 0" () {
        given:
            HttpResponse response = new HttpResponse(200, "<span class=\"ProfileNav-value\" data-count=\"200\"></span>")
        when:
            int following = service.getCurrentlyFollowing()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == 0
    }

    def "when fetches correct html, then returns expected number" () {
        given:
            HttpResponse response = new HttpResponse(200, "<span class=\"ProfileNav-value\" data-count=\"200\"></span><span class=\"ProfileNav-value\" data-count=\"699\"></span>")
        when:
            int following = service.getCurrentlyFollowing()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == 699
    }

}
