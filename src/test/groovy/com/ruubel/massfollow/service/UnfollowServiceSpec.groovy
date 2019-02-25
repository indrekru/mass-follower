package com.ruubel.massfollow.service

import com.ruubel.massfollow.config.ConfigParams
import com.ruubel.massfollow.model.Followed
import com.ruubel.massfollow.service.http.HttpRequestService
import spock.lang.Specification

import java.time.Instant

class UnfollowServiceSpec extends Specification {

    UnfollowService service
    FollowPersistenceService followedService
    HttpRequestService httpRequestService
    HeaderService headersService
    ConfigParams configParams

    def setup () {
        followedService = Mock(FollowPersistenceService)
        headersService = Mock(HeaderService)
        httpRequestService = Mock(HttpRequestService)
        configParams = Mock(ConfigParams)
        service = new UnfollowService(followedService, headersService, httpRequestService, configParams)
    }

    def "when followed is null, then shouldUnfollow returns false" () {
        when:
            boolean shouldUnfollow = service.shouldUnfollow(null)
        then:
            !shouldUnfollow
    }

    def "when followed date is less that 2 days, then shouldUnfollow returns false" () {
        given:
            Followed followed = new Followed("external_name", "123")
            followed.followed = Instant.now().minusSeconds(172799) // 1 second less than 2 days
        when:
            boolean shouldUnfollow = service.shouldUnfollow(followed)
        then:
            !shouldUnfollow
    }

    def "when followed date is exactly 2 days, then shouldUnfollow returns true" () {
        given:
            Followed followed = new Followed("external_name", "123")
            followed.followed = Instant.now().minusSeconds(172800) // Exactly 2 days
        when:
            boolean shouldUnfollow = service.shouldUnfollow(followed)
        then:
            shouldUnfollow
    }

    def "when followed date is more than 2 days, then shouldUnfollow returns true" () {
        given:
            Followed followed = new Followed("external_name", "123")
            followed.followed = Instant.now().minusSeconds(172801) // 1 second more than 2 days
        when:
            boolean shouldUnfollow = service.shouldUnfollow(followed)
        then:
            shouldUnfollow
    }

}
