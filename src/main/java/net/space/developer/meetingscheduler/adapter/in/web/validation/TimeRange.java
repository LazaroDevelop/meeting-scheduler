package net.space.developer.meetingscheduler.adapter.in.web.validation;

import java.time.Instant;

/**
 * Any request carrying a time interval that {@link ValidTimeRange} can check.
 */
public interface TimeRange {

    Instant startTime();

    Instant endTime();
}
