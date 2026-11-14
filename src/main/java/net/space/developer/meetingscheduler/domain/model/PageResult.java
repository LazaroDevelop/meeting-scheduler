package net.space.developer.meetingscheduler.domain.model;

import java.util.List;

/**
 * Framework-agnostic slice of a larger result set. 
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public record PageResult<T>(List<T> content, int page, int size, long totalElements) {

    public PageResult {
        content = List.copyOf(content);
    }

    public int totalPages() {
        return (int) Math.ceilDiv(totalElements, size);
    }
}
