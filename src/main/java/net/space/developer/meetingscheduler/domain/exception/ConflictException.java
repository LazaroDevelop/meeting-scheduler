package net.space.developer.meetingscheduler.domain.exception;

/**
 * Exception thrown when a conflict occurs in the domain, such as when attempting to create a booking that conflicts with an existing booking or when attempting to cancel a booking that is already cancelled.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public abstract class ConflictException extends DomainException {

    /**
     * Constructs a new ConflictException with the specified detail message.
     * 
     * @param message the detail message
     */
    protected ConflictException(String message) {
        super(message);
    }
}
