package meli.simian.rest.validator;

import meli.simian.common.validator.DnaValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class DNAConstraintValidator implements ConstraintValidator<ValidDNAMatrix, String[]> {

    DnaValidator validator;
    public DNAConstraintValidator(DnaValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(String[] rows, ConstraintValidatorContext context) {
        Set<String> errors = validator.checkForErrors(rows);
        if(errors.size() > 0) {
            populateErrorMessages(context, errors);
            return false;
        }

        return true;
    }

    private void populateErrorMessages(ConstraintValidatorContext ctx, Set<String> messages) {
        ctx.disableDefaultConstraintViolation();

        messages.forEach(message -> ctx.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation());
    }
}
