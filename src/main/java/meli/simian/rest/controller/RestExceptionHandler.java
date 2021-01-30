package meli.simian.rest.controller;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.exception.ValidationException;
import meli.simian.rest.model.ErrorPayload;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorPayload errorPayload = new ErrorPayload();

        errorPayload.setCode(status.value());
        errorPayload.setMessage(status.getReasonPhrase());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorPayload.setErrors(new HashSet<>(errors));

        return new ResponseEntity<>(errorPayload, headers, status);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ErrorPayload> handleValidationException(ValidationException ex) {
        log.error("Validation exception was thrown, building error payload");

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorPayload response = new ErrorPayload();
        response.setCode(status.value());
        response.setMessage(ex.getMessage());
        response.setErrors(ex.getErrors());

        return ResponseEntity.status(status)
                .body(response);
    }

    // Handling generic exception to avoid showing the exception to the final user
    // mitigating a possible security flaw
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorPayload> handleGenericExceptionHandler(Exception ex) {
        log.error("A generic exception was thrown, building error payload, ex={}",ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorPayload response = new ErrorPayload();
        response.setCode(status.value());
        response.setMessage("An unknown error occurred, please contact the application administrator.");

        return ResponseEntity.status(status)
                .body(response);
    }
}
