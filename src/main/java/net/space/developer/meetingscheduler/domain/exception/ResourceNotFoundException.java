package net.space.developer.meetingscheduler.domain.exception;

/**
 * A requested aggregate does not exist.
 */
public abstract class ResourceNotFoundException extends DomainException {

    protected ResourceNotFoundException(String resource, Object id) {
        super("%s with id %s was not found".formatted(resource, id));
    }
}
