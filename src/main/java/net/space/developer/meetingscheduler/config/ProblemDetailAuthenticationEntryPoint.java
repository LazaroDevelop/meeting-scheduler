package net.space.developer.meetingscheduler.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.space.developer.meetingscheduler.adapter.in.web.common.Problems;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * Answers unauthenticated requests with a Basic challenge and the API's ProblemDetail body.
 */
class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * The Basic challenge header value sent to unauthenticated requests.
     */
    private static final String CHALLENGE = "Basic realm=\"meeting-scheduler\"";

    /**
     * The JSON mapper used to serialize the ProblemDetail body.
     */
    private final JsonMapper jsonMapper;

    /**
     * Copy Constructor for ProblemDetailAuthenticationEntryPoint.
     */
    ProblemDetailAuthenticationEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * Commences the authentication process for unauthenticated requests.
     *
     * @param request        the HTTP servlet request
     * @param response       the HTTP servlet response
     * @param authException the authentication exception
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ProblemDetail problem = Problems.of(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Valid credentials are required to access this resource", LocalDateTime.now());
        problem.setInstance(URI.create(request.getRequestURI()));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, CHALLENGE);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        jsonMapper.writeValue(response.getOutputStream(), problem);
    }
}
