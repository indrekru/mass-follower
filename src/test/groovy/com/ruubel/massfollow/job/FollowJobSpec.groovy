package com.ruubel.massfollow.job

import com.ruubel.massfollow.service.FollowService
import com.ruubel.massfollow.service.UnfollowService
import spock.lang.Specification

class FollowJobSpec extends Specification {

    FollowJob job
    FollowService followService
    UnfollowService unfollowService

    def setup () {
        followService = Mock(FollowService)
        unfollowService = Mock(UnfollowService)
        job = new FollowJob(followService, unfollowService)
    }

    def "when following less than 3500, then runs follow and unfollow once" () {
        when:
            job.doLogic(0)
        then:
            1 * followService.getCurrentlyFollowing() >> 3499
            1 * followService.execute(_)
            1 * unfollowService.execute()

    }

    def "when following returns more than 3500 twice, then gives up" () {
        when:
            job.doLogic(0)
        then:
            2 * followService.getCurrentlyFollowing() >> 3500
            1 * unfollowService.execute()
            0 * followService.execute(_)
    }

    def "when following returns more than 3500 once, then runs follow once" () {
        when:
            job.doLogic(0)
        then:
            1 * followService.getCurrentlyFollowing() >> 3500
            1 * followService.getCurrentlyFollowing() >> 3000
            1 * unfollowService.execute()
            1 * followService.execute(_)
    }

}
