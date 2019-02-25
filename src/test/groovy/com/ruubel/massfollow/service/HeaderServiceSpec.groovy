package com.ruubel.massfollow.service

import com.ruubel.massfollow.config.ConfigParams
import org.springframework.http.HttpHeaders
import spock.lang.Specification

class HeaderServiceSpec extends Specification {

    HeaderService service
    ConfigParams configParams

    private String ACCOUNT = "account_name"

    def setup () {
        configParams = Mock(ConfigParams)
        service = new HeaderService(configParams)
    }

    def "when requesting homeFollowingPageHtmlHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getHomeFollowersPageHtmlHeaders()
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
            headers.containsKey("accept")
            headers.containsKey("cache-control")
            headers.containsKey("upgrade-insecure-requests")
            headers.containsKey("referer")
    }

    def "when requesting commonHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getCommonHeaders()
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
    }

    def "when requesting nextFollowerBatchJsonHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getNextFollowerBatchJsonHeaders(ACCOUNT)
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
            headers.containsKey("pragma")
            headers.containsKey("accept")
            headers.containsKey("x-requested-with")
            headers.containsKey("x-twitter-active-user")
            headers.containsKey("referer")
            headers.get("referer").get(0) == "https://twitter.com/" + ACCOUNT + "/followers"
    }

    def "when requesting followersPageHtmlHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getAccountFollowersPageHtmlHeaders(ACCOUNT)
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
            headers.containsKey("accept")
            headers.containsKey("cache-control")
            headers.containsKey("upgrade-insecure-requests")
            headers.containsKey("referer")
            headers.get("referer").get(0) == "https://twitter.com/" + ACCOUNT + "/followers"
    }

    def "when requesting followActionHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getFollowActionHeaders()
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
            headers.containsKey("accept")
            headers.containsKey("content-type")
            headers.containsKey("origin")
            headers.containsKey("x-twitter-auth-type")
            headers.containsKey("x-twitter-active-user")
            headers.containsKey("dnt")
            headers.containsKey("cache-control")
            headers.containsKey("x-csrf-token")
            headers.containsKey("authorization")
    }

    def "when requesting nextHomeFollowerBatchJsonHeaders, then returns well-defined essential headers" () {
        when:
            HttpHeaders headers = service.getNextHomeFollowerBatchJsonHeaders()
        then:
            headers.containsKey("authority")
            headers.containsKey("user-agent")
            headers.containsKey("cookie")
            headers.containsKey("accept-language")
            headers.containsKey("accept-encoding")
            headers.containsKey("accept")
            headers.containsKey("x-requested-with")
            headers.containsKey("x-twitter-active-user")
            headers.containsKey("referer")
            headers.get("referer").get(0) == "https://twitter.com/following"
    }

}
