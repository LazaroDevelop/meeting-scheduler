package net.space.developer.meetingscheduler.domain.model;

public record Room(Long id, String name, int capacity, String location) {

    public static Room create(String name, int capacity, String location) {
        return new Room(null, name, capacity, location);
    }
}
