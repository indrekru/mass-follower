package com.ruubel.massfollow.job;

import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.FollowPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class CleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private int limit;

    private FollowPersistenceService followPersistenceService;

    @Autowired
    public CleanupJob(FollowPersistenceService followPersistenceService) {
        this.followPersistenceService = followPersistenceService;
        this.limit = 8500;
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public List<Followed> cleanup() {
        log.info("Starting cleanup");

        List<Followed> followedList = followPersistenceService.findAll();

        if (followedList.size() > limit) {
            int elementsToRemove = followedList.size() - limit;
            Iterator<Followed> itr = followedList.iterator();
            List<Followed> removeElements = new ArrayList<>();
            int i = 0;
            while (itr.hasNext() && i < elementsToRemove) {
                Followed followed = itr.next();
                followPersistenceService.delete(followed);
                removeElements.add(followed);
                i++;
            }
            followedList.removeAll(removeElements);
        }

        log.info("Done with the cleanup");
        return followedList;
    }
}
