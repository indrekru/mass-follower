package com.ruubel.massfollow.job

import com.ruubel.massfollow.model.Followed
import com.ruubel.massfollow.service.FollowPersistenceService
import spock.lang.Specification

class FollowedCleanupJobSpec extends Specification {

    FollowedCleanupJob cleanupJob
    FollowPersistenceService followPersistenceService

    def setup () {
        followPersistenceService = Mock(FollowPersistenceService)
        cleanupJob = new FollowedCleanupJob(followPersistenceService)
    }

    def "when limit is 2 and 6 elements found, then removes 4 and returns 2" () {
        given:
            List<Followed> all = new ArrayList<Followed>(){{
                add(new Followed())
                add(new Followed())
                add(new Followed())
                add(new Followed())
                add(new Followed())
                add(new Followed())
            }}
            cleanupJob.limit = 2
        when:
            List<Followed> result = cleanupJob.cleanup()
        then:
            1 * followPersistenceService.findAll() >> all
            4 * followPersistenceService.delete(_)
            result.size() == 2
    }

    def "when limit is 2 and 1 elements found, then returns 1" () {
        given:
            List<Followed> all = new ArrayList<Followed>(){{
                add(new Followed())
            }}
            cleanupJob.limit = 2
        when:
            List<Followed> result = cleanupJob.cleanup()
        then:
            1 * followPersistenceService.findAll() >> all
            0 * followPersistenceService.delete(_)
            result.size() == 1
    }

    def "when limit is 2 and 2 elements found, then returns 2" () {
        given:
            List<Followed> all = new ArrayList<Followed>(){{
                add(new Followed())
                add(new Followed())
            }}
            cleanupJob.limit = 2
        when:
            List<Followed> result = cleanupJob.cleanup()
        then:
            1 * followPersistenceService.findAll() >> all
            0 * followPersistenceService.delete(_)
            result.size() == 2
    }

}
