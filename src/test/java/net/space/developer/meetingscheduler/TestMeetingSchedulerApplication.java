package net.space.developer.meetingscheduler;

import org.springframework.boot.SpringApplication;

public class TestMeetingSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.from(MeetingSchedulerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
