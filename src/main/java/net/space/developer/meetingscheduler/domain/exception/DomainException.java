package net.space.developer.meetingscheduler.domain.exception;

/**
 * Base type for every business rule violation raised by the domain.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Constructs a new DomainException with the specified detail message.
     * 
     * @param message the detail message
     */
    protected DomainException(String message) {
        super(message);
    }
}
