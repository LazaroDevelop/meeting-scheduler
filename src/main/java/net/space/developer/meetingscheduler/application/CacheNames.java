package net.space.developer.meetingscheduler.application;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Names of the read caches maintained by the application services
 * (must match {@code spring.cache.cache-names}).
 */
final class CacheNames {

    /**
     * The cache name for rooms.
     */
    static final String ROOMS = "rooms";

    /**
     * The cache name for finding a room by its ID.
     */
    static final String ROOM_BY_ID = "roomById";

    /**
     * The cache name for bookings.
     */
    static final String BOOKING_BY_ID = "bookingById";

    /**
     * The cache name for searching bookings.
     */
    static final String BOOKING_SEARCH = "bookingSearch";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CacheNames() {
    }
}
