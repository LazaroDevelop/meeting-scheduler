package net.space.developer.meetingscheduler.adapter.in.web.validation;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Any request carrying a time interval that {@link ValidTimeRange} can check.
 */
public interface TimeRange {

    Instant startTime();

    Instant endTime();
}
