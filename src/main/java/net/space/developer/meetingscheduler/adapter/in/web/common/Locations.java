package net.space.developer.meetingscheduler.adapter.in.web.common;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Builds the {@code Location} header of a newly created resource relative to the current request.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public final class Locations {

    private Locations() {
    }

    public static URI of(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
