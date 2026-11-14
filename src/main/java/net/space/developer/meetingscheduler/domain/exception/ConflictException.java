package net.space.developer.meetingscheduler.domain.exception;

/**
 * The request clashes with the current state of the system.
 */
public abstract class ConflictException extends DomainException {

    protected ConflictException(String message) {
        super(message);
    }
}
