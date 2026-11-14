package net.space.developer.meetingscheduler.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief API users allowed to perform write operations, bound from {@code app.security.users}.
 */
@Validated
@ConfigurationProperties("app.security")
public record SecurityProperties(@NotEmpty List<@Valid User> users) {

    public record User(@NotBlank String username, @NotBlank String password) {
    }
}
