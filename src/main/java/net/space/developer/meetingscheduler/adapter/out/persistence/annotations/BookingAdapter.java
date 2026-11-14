package net.space.developer.meetingscheduler.adapter.out.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.springframework.stereotype.Component;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief BookingAdapter annotation to mark classes that serve as adapters for booking-related persistence operations.
 */

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingAdapter {
}
