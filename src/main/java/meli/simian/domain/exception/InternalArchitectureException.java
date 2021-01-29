package meli.simian.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//, reason = "Internal architecture error, please contact the developer(s)."
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalArchitectureException extends RuntimeException {
    public InternalArchitectureException(String message) {
        super(message);
    }
}
