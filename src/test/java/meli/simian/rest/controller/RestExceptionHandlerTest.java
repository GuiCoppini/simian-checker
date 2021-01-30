package meli.simian.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import meli.simian.domain.exception.ValidationException;
import meli.simian.rest.model.ErrorPayload;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;

class RestExceptionHandlerTest {
    // there is no need to test the MethodArgumentNotValid method because the controller integration test does that

    // however, the ValidationException in only thrown if the services are called with invalid domain objects

    RestExceptionHandler handler = new RestExceptionHandler();

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void validationExceptionBuildsErrorPayload_400() {
        HashSet<String> errorSet = new HashSet<>();
        String errorMessage = "Error message";
        ValidationException exampleException = new ValidationException(errorMessage, errorSet);

        ResponseEntity<ErrorPayload> response = handler.handleValidationException(exampleException);

        ErrorPayload body = response.getBody();
        assertEquals(errorMessage, body.getMessage());
        assertEquals(errorSet, body.getErrors());

    }

    @Test
    void genericGenericExceptionBuildsErrorPayload_500() throws JsonProcessingException {
        String errorMessage = "Exception message which should NOT be shown to the user";
        ValidationException exampleException = new ValidationException(errorMessage);

        ResponseEntity<ErrorPayload> response = handler.handleGenericExceptionHandler(exampleException);

        String json = mapper.writeValueAsString(response);

        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getCode());
        assertThat(json, not(containsString(errorMessage)));
        assertThat(json, not("errors"));

    }
}