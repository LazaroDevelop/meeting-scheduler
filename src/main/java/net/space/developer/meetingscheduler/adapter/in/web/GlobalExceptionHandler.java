package net.space.developer.meetingscheduler.adapter.in.web;

import net.space.developer.meetingscheduler.adapter.in.web.common.FieldViolation;
import net.space.developer.meetingscheduler.adapter.in.web.common.Problems;
import net.space.developer.meetingscheduler.domain.exception.ConflictException;
import net.space.developer.meetingscheduler.domain.exception.InvalidTimeRangeException;
import net.space.developer.meetingscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Translates every exception leaving the web layer into an RFC 9457 {@link ProblemDetail}.
 * Framework errors not overridden here (405, 406, 415, unknown routes...) keep the
 * {@link ResponseEntityExceptionHandler} defaults, which are ProblemDetails as well.
 */
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles the ResourceNotFoundException and returns a ProblemDetail with a 404 status code.
     * @param ex - The ResourceNotFoundException that was thrown.
     * @return A ProblemDetail with a 404 status code and a message indicating that the resource was not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return Problems.of(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    /**
     * Handles the ConflictException and returns a ProblemDetail with a 409 status code.
     * @param ex - The ConflictException that was thrown.
     * @return A ProblemDetail with a 409 status code and a message indicating that there was a conflict with the request.
     */
    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleConflict(ConflictException ex) {
        return Problems.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    /**
     * Handles the InvalidTimeRangeException and returns a ProblemDetail with a 400 status code.
     * @param ex - The InvalidTimeRangeException that was thrown.
     * @return A ProblemDetail with a 400 status code and a message indicating that the time range provided in the request was invalid.
     */
    @ExceptionHandler(InvalidTimeRangeException.class)
    ProblemDetail handleInvalidTimeRange(InvalidTimeRangeException ex) {
        return Problems.of(HttpStatus.BAD_REQUEST, Problems.VALIDATION_FAILED, ex.getMessage());
    }

    /**
     * Handles the DataIntegrityViolationException and returns a ProblemDetail with a 409 status code.
     * Safety net for races the application checks cannot see (e.g. two rooms created with the same name at once).
     * 
     * @param ex - The DataIntegrityViolationException that was thrown.
     * @return A ProblemDetail with a 409 status code and a message indicating that the request conflicts with existing data.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation", ex);
        return Problems.of(HttpStatus.CONFLICT, "Conflict", "The request conflicts with existing data");
    }

    /**
     * Handles unexpected exceptions and returns a ProblemDetail with a 500 status code.
     * @param ex - The unexpected exception that was thrown.
     * @return A ProblemDetail with a 500 status code and a message indicating that an unexpected error occurred.
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        logger.error("Unexpected error", ex);
        return Problems.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", "An unexpected error occurred");
    }

    /**
     * Handles method argument validation errors and returns a ProblemDetail with a 400 status code.
     * @param ex - The MethodArgumentNotValidException that was thrown.
     * @param headers - The HTTP headers.
     * @param status - The HTTP status code.
     * @param request - The web request.
     * @return A ProblemDetail with a 400 status code and a list of field violations.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return badRequest(ex.getBindingResult().getAllErrors().stream().map(FieldViolation::from).toList());
    }

    /**
     * Handles handler method validation errors and returns a ProblemDetail with a 400 status code.
     * @param ex - The HandlerMethodValidationException that was thrown.
     * @param headers - The HTTP headers.
     * @param status - The HTTP status code.
     * @param request - The web request.
     * @return A ProblemDetail with a 400 status code and a list of field violations.
     */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return badRequest(ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldViolation(
                                result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
                .toList());
    }

    /**
     * Handles type mismatch errors and returns a ProblemDetail with a 400 status code.
     * @param ex - The TypeMismatchException that was thrown.
     * @param headers - The HTTP headers.
     * @param status - The HTTP status code.
     * @param request - The web request.
     * @return A ProblemDetail with a 400 status code and a list of field violations indicating the invalid value.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String field = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : ex.getPropertyName();
        return badRequest(List.of(new FieldViolation(field, "has an invalid value '%s'".formatted(ex.getValue()))));
    }

    /**
     * Handles unreadable HTTP message errors and returns a ProblemDetail with a 400 status code.
     * @param ex - The HttpMessageNotReadableException that was thrown.
     * @param headers - The HTTP headers.
     * @param status - The HTTP status code.
     * @param request - The web request.
     * @return A ProblemDetail with a 400 status code and a message indicating that the request body is missing or not valid JSON.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest().body(Problems.of(HttpStatus.BAD_REQUEST, "Malformed request",
                "The request body is missing or is not valid JSON"));
    }

    /**
     * Handles bad request errors and returns a ProblemDetail with a 400 status code.
     * @param errors - The list of field violations that caused the bad request.
     * @return A ResponseEntity containing a ProblemDetail with a 400 status code and a list of field violations.
     */
    private static ResponseEntity<Object> badRequest(List<FieldViolation> errors) {
        return ResponseEntity.badRequest().body(Problems.validation(errors));
    }
}
