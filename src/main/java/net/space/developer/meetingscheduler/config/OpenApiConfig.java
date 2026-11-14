package net.space.developer.meetingscheduler.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import net.space.developer.meetingscheduler.adapter.in.web.common.ApiDocs;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * OpenAPI metadata served at {@code /v3/api-docs} and rendered by Swagger UI at {@code /swagger-ui.html}.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Meeting Scheduler API",
        version = "v1",
        description = "Book meeting rooms. Queries are public; creating rooms and creating or "
                + "cancelling bookings require HTTP Basic credentials."))
@SecurityScheme(name = ApiDocs.BASIC_AUTH, type = SecuritySchemeType.HTTP, scheme = "basic")
public class OpenApiConfig {
}
