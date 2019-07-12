package com.ruubel.massfollow;

import com.ruubel.massfollow.job.FollowJob;
import com.ruubel.massfollow.job.FollowStatsCleanupJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    @Autowired
    private FollowJob followJob;

    @Autowired
    private FollowStatsCleanupJob followStatsCleanupJob;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) {
        followJob.execute();
        followStatsCleanupJob.cleanup();
    }

}
