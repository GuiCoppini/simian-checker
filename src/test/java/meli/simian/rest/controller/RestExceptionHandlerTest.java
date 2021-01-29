package meli.simian.rest.controller;

import meli.simian.domain.exception.ValidationException;
import meli.simian.rest.model.ErrorPayload;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

class RestExceptionHandlerTest {
    // there is no need to test the MethodArgumentNotValid method because the controller integration test does that

    // however, the ValidationException in only thrown if the services are called with invalid domain objects

    RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void validationExceptionBuildsErrorPayload() {
        HashSet<String> errorSet = new HashSet<>();
        String errorMessage = "Error message";
        ValidationException exampleException = new ValidationException(errorMessage, errorSet);

        ResponseEntity<ErrorPayload> response = handler.handleValidationException(exampleException);

        ErrorPayload body = response.getBody();
        assertEquals(errorMessage, body.getMessage());
        assertEquals(errorSet, body.getErrors());

    }

}