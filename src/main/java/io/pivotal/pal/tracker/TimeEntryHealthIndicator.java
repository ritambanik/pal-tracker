package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    TimeEntryRepository timeEntryRepository;
    private final static int MAX = 5;

    TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        if (timeEntryRepository.list().size() >= MAX) {
            builder.down();
        } else {
            builder.up();
        }
        return builder.build();
    }
}
