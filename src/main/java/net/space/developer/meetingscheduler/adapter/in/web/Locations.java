package net.space.developer.meetingscheduler.adapter.in.web;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Builds the {@code Location} header of a newly created resource relative to the current request.
 */
final class Locations {

    private Locations() {
    }

    static URI of(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
