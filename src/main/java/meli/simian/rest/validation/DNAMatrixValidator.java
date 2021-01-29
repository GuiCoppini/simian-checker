package meli.simian.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class DNAMatrixValidator implements ConstraintValidator<ValidDNAMatrix, String[]> {
    public static final String DNA_BASES = "ACTG";

    @Override
    public boolean isValid(String[] rows, ConstraintValidatorContext context) {

        // Using the Set avoids duplicate messages
        Set<String> errorMessages = new HashSet<>();

        boolean valid = true;
        // empty DNA sequence
        if (rows.length < 1) {
            valid = false;
            errorMessages.add("DNA sequence must not be empty");
        }

        // checking for invalid sizes and regex
        for (String row : rows) {
            // not square
            if (row.length() != rows.length) {
                valid = false;
                errorMessages.add("DNA sequence matrix must be square");
            }

            // checking for invalid DNA bases (A C T G)
            if (!row.matches("[" + DNA_BASES + "]*")) {
                errorMessages.add("DNA sequence must only contain the characters \"A\", \"C\", \"T\" and \"G\"");
                valid = false;
            }
        }

        populateErrorMessages(context, errorMessages);
        return valid;
    }

    private void populateErrorMessages(ConstraintValidatorContext ctx, Set<String> messages) {
        ctx.disableDefaultConstraintViolation();

        messages.forEach(message -> ctx.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation());
    }
}
