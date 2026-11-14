package net.space.developer.meetingscheduler.domain.exception;

/**
 * Base type for every business rule violation raised by the domain.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
