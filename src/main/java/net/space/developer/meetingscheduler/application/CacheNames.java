package net.space.developer.meetingscheduler.application;

/**
 * Names of the read caches maintained by the application services
 * (must match {@code spring.cache.cache-names}).
 */
final class CacheNames {

    static final String ROOMS = "rooms";
    static final String ROOM_BY_ID = "roomById";
    static final String BOOKING_BY_ID = "bookingById";
    static final String BOOKING_SEARCH = "bookingSearch";

    private CacheNames() {
    }
}
