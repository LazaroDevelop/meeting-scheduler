package net.space.developer.meetingscheduler.adapter.in.web.dto;

import net.space.developer.meetingscheduler.domain.model.PageResult;

import java.util.List;
import java.util.function.Function;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief PageResponse is a generic response DTO for paginated results.
 * @param <T> - The type of the content in the page.
 * @param content - The list of items in the current page.
 * @param page - The current page number (0-based).
 * @param size - The number of items per page.
 * @param totalElements - The total number of items across all pages.
 * @param totalPages - The total number of pages available.
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <D, T> PageResponse<T> from(PageResult<D> result, Function<D, T> mapper) {
        return new PageResponse<>(result.content().stream().map(mapper).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }
}
