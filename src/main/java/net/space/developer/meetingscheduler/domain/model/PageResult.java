package net.space.developer.meetingscheduler.domain.model;

import java.util.List;

/**
 * Framework-agnostic slice of a larger result set.
 */
public record PageResult<T>(List<T> content, int page, int size, long totalElements) {

    public PageResult {
        content = List.copyOf(content);
    }

    public int totalPages() {
        return (int) Math.ceilDiv(totalElements, size);
    }
}
