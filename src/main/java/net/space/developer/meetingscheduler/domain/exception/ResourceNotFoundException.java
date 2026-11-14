package net.space.developer.meetingscheduler.domain.exception;

/**
 * A requested aggregate does not exist.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public abstract class ResourceNotFoundException extends DomainException {

    /**
     * Constructs a new ResourceNotFoundException with a message indicating the resource type and ID that was not found.
     * 
     * @param resource the type of the resource that was not found
     * @param id the ID of the resource that was not found
     */
    protected ResourceNotFoundException(String resource, Object id) {
        super("%s with id %s was not found".formatted(resource, id));
    }
}
