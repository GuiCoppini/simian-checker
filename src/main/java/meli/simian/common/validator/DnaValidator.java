package meli.simian.common.validator;

import meli.simian.domain.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

// yes it looks like it has duplicated logic from the rest validation, but the service layer must work by himself
// agnostic of the "entry point" of the system
@Service
public class DnaValidator implements GenericValidator<String[]> {

    private static final String DNA_BASES = "ACTG";

    @Override
    public Set<String> checkForErrors(String[] object) {
        Set<String> errorMessages = new HashSet<>();

        if (object == null || object.length < 1) {
            errorMessages.add("DNA sequence must not be empty");
            // cannot continue from here on
            return errorMessages;
        }

        // checking for invalid sizes and regex
        for (String row : object) {
            // not square
            if (row.length() != object.length) {
                errorMessages.add("DNA sequence matrix must be square");
            }

            // checking for invalid DNA bases (A C T G)
            if (!row.matches("[" + DNA_BASES + "]*")) {
                errorMessages.add("DNA sequence must only contain the characters 'A', 'C', 'T' and 'G'");
            }
        }

        return errorMessages;
    }

    @Override
    public void validate(String[] object) {
        String MESSAGE = "Invalid DNA sequence";

        Set<String> errors = this.checkForErrors(object);

        if (errors.size() > 0) {
            throw new ValidationException(MESSAGE, errors);
        }
    }
}
