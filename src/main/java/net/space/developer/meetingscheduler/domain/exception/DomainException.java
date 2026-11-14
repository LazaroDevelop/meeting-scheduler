package net.space.developer.meetingscheduler.domain.exception;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Base type for every business rule violation raised by the domain.
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
