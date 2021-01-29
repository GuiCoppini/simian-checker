package meli.simian.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorPayload {
    List<Error> errors;
}

@Data
class Error {
    String code;
    String message;
}