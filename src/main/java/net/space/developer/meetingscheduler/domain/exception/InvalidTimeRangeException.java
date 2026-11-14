package net.space.developer.meetingscheduler.domain.exception;

/**
 * Exception thrown when an invalid time range is provided, such as when the start time is after the end time.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public class InvalidTimeRangeException extends DomainException {

    /**
     * Constructs a new InvalidTimeRangeException with the specified detail message.
     * 
     * @param message the detail message
     */
    public InvalidTimeRangeException(String message) {
        super(message);
    }
}
