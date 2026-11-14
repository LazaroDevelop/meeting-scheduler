package net.space.developer.meetingscheduler.domain.port.in;

public record CreateRoomCommand(String name, int capacity, String location) {
}
