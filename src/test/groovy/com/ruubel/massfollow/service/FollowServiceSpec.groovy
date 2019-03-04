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

    def "when fails to connect, then returns [0, 0]" () {
        given:
            HttpResponse response = new HttpResponse(500, "exception")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [0, 0]
    }

    def "when fetches correct html, but no following element found, then returns 0" () {
        given:
            HttpResponse response = new HttpResponse(200, "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"following-not\" href=\"/freenancefeed/following\" data-original-title=\"3,071 Following\">\n" +
                    "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Following</span>\n" +
                    "            <span class=\"u-hiddenVisually\">Following</span>\n" +
                    "          <span class=\"ProfileNav-value\" data-count=\"3071\" data-is-compact=\"false\">3,071</span>\n" +
                    "        </a>")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [0, 0]
    }

    def "when fetches correct html, but no followers element found, then returns 0" () {
        given:
            HttpResponse response = new HttpResponse(200, "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"followers-not\" href=\"/freenancefeed/followers\" data-original-title=\"3,149 Followers\">\n" +
                    "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Followers</span>\n" +
                    "            <span class=\"u-hiddenVisually\">Followers</span>\n" +
                    "          <span class=\"ProfileNav-value\" data-count=\"3149\" data-is-compact=\"false\">3,149</span>\n" +
                    "        </a>")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [0, 0]
    }

    def "when fetches correct html and following element found, then returns the number" () {
        given:
            HttpResponse response = new HttpResponse(200, "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"following\" href=\"/freenancefeed/following\" data-original-title=\"3,071 Following\">\n" +
                "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Following</span>\n" +
                "            <span class=\"u-hiddenVisually\">Following</span>\n" +
                "          <span class=\"ProfileNav-value\" data-count=\"3071\" data-is-compact=\"false\">3,071</span>\n" +
                "        </a>")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [3071, 0]
    }

    def "when fetches correct html and followers element found, then returns the number" () {
        given:
            HttpResponse response = new HttpResponse(200, "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"followers\" href=\"/freenancefeed/followers\" data-original-title=\"3,149 Followers\">\n" +
                "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Followers</span>\n" +
                "            <span class=\"u-hiddenVisually\">Followers</span>\n" +
                "          <span class=\"ProfileNav-value\" data-count=\"3149\" data-is-compact=\"false\">3,149</span>\n" +
                "        </a>")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [0, 3149]
    }

    def "when fetches correct html and following and followers element found, then returns the numbers" () {
        given:
            HttpResponse response = new HttpResponse(200,
                    "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"following\" href=\"/freenancefeed/following\" data-original-title=\"3,071 Following\">\n" +
                            "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Following</span>\n" +
                            "            <span class=\"u-hiddenVisually\">Following</span>\n" +
                            "          <span class=\"ProfileNav-value\" data-count=\"3071\" data-is-compact=\"false\">3,071</span>\n" +
                            "        </a>" +
                    "<a class=\"ProfileNav-stat ProfileNav-stat--link u-borderUserColor u-textCenter js-tooltip js-openSignupDialog js-nonNavigable u-textUserColor\" data-nav=\"followers\" href=\"/freenancefeed/followers\" data-original-title=\"3,149 Followers\">\n" +
                "          <span class=\"ProfileNav-label\" aria-hidden=\"true\">Followers</span>\n" +
                "            <span class=\"u-hiddenVisually\">Followers</span>\n" +
                "          <span class=\"ProfileNav-value\" data-count=\"3149\" data-is-compact=\"false\">3,149</span>\n" +
                "        </a>")
        when:
            long[] following = service.getImFollowingAndMyFollowers()
        then:
            1 * httpRequestService.exchange(_, _, _, _) >> response
            following == [3071, 3149]
    }

}
