package net.space.developer.meetingscheduler.adapter.out.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * RoomAdapter annotation to mark classes that serve as adapters for room-related persistence operations.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoomAdapter {
}
