package net.space.developer.meetingscheduler.adapter.in.web.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Single place where the API's {@link ProblemDetail} bodies are built.
 */
public final class Problems {

    public static final String VALIDATION_FAILED = "Validation failed";

    private Problems() {
    }

    public static ProblemDetail of(HttpStatus status, String title, String detail, LocalDateTime timestamp) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", timestamp);
        return problem;
    }

    public static ProblemDetail validation(List<FieldViolation> errors) {
        ProblemDetail problem = of(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, "Request contains invalid fields", LocalDateTime.now());
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}
