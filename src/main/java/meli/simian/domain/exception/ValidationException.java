package meli.simian.domain.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ValidationException extends RuntimeException {
    private Set<String> errors = new HashSet<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Set<String> errors) {
        super(message);
        this.errors = errors;
    }

    void addError(String msg) {
        this.errors.add(msg);
    }
}
