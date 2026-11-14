package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.BookingAlreadyCancelledException;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Represents a booking in the meeting scheduler system.
 * 
 * @param id - the unique identifier of the booking
 * @param roomId - the unique identifier of the room associated with the booking
 * @param personInCharge - the name of the person responsible for the booking
 * @param slot - the time slot for which the booking is made
 * @param status - the status of the booking (ACTIVE or CANCELLED)
 */
public record Booking(Long id, Long roomId, String personInCharge, TimeSlot slot, BookingStatus status) {

    /**
     * Creates a new Booking instance with the specified room ID, person in charge, and time slot.
     * The booking is initialized with an ACTIVE status.
     * 
     * @param roomId the ID of the room associated with the booking
     * @param personInCharge the name of the person responsible for the booking
     * @param slot the time slot for which the booking is made
     * @return a new Booking instance
     */
    public static Booking create(Long roomId, String personInCharge, TimeSlot slot) {
        return new Booking(null, roomId, personInCharge, slot, BookingStatus.ACTIVE);
    }

    /**
     * Checks if the booking is currently active.
     * 
     * @return true if the booking is active, false otherwise
     */
    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    /**
     * Cancels the booking if it is currently active.
     * If the booking is already cancelled, a BookingAlreadyCancelledException is thrown.
     * 
     * @return a new Booking instance with the status set to CANCELLED
     * @throws BookingAlreadyCancelledException if the booking is already cancelled
     */
    public Booking cancel() {
        if (!isActive()) {
            throw new BookingAlreadyCancelledException(id);
        }
        return new Booking(id, roomId, personInCharge, slot, BookingStatus.CANCELLED);
    }
}
