package com.ruubel.massfollow.job

import com.ruubel.massfollow.service.FollowService
import com.ruubel.massfollow.service.FollowingAmountService
import com.ruubel.massfollow.service.MailingService
import com.ruubel.massfollow.service.UnfollowService
import spock.lang.Specification

class FollowJobSpec extends Specification {

    FollowJob job
    FollowService followService
    UnfollowService unfollowService
    FollowingAmountService followingAmountService
    MailingService mailingService

    def setup () {
        followService = Mock(FollowService)
        unfollowService = Mock(UnfollowService)
        followingAmountService = Mock(FollowingAmountService)
        mailingService = Mock(MailingService)
        job = new FollowJob(followService, unfollowService, followingAmountService, mailingService)
    }

    def "when execute is run and followers are done first, then after updates followers in DB" () {
        when:
            job.execute()
        then:
            1 * followService.getImFollowingAndMyFollowers() >> [3499, 2000]
            1 * followService.execute(_)
            1 * followService.getImFollowingAndMyFollowers() >> [3666, 2666]
            1 * followingAmountService.saveFollowingAmounts(3666, 2666)
            0 * mailingService.notifyRecaptchaBlock()
    }

    def "when following less than 3500, then runs follow and unfollow once" () {
        when:
            job.doLogic(0)
        then:
            1 * followService.getImFollowingAndMyFollowers() >> [3499, 2000]
            1 * followService.execute(_)
            0 * mailingService.notifyRecaptchaBlock()
    }

    def "when following returns more than 3500 twice, then gives up" () {
        when:
            job.doLogic(0)
        then:
            2 * followService.getImFollowingAndMyFollowers() >> [3500, 2000]
            1 * unfollowService.execute()
            0 * followService.execute(_)
            0 * mailingService.notifyRecaptchaBlock()
    }

    def "when following returns more than 3500 once, then runs follow once" () {
        when:
            job.doLogic(0)
        then:
            1 * followService.getImFollowingAndMyFollowers() >> [3500, 2000]
            1 * followService.getImFollowingAndMyFollowers() >> [3000, 2000]
            1 * unfollowService.execute()
            1 * followService.execute(_)
            0 * mailingService.notifyRecaptchaBlock()
    }

    def "when imFollowing returns 0, then it's recaptcha block and notifies via email" () {
        when:
            job.execute()
        then:
            2 * followService.getImFollowingAndMyFollowers() >> [0, 2000]
            1 * followService.execute(_)
            1 * mailingService.notifyRecaptchaBlock()
    }

}
