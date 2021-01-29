package meli.simian.common.validator;

import meli.simian.rest.validator.DNAConstraintValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DNAConstraintValidatorTest {

    // the mock of the validator that does the real validation, this class being tested only adds the errors
    // to the context
    DnaValidator mockedValidator = mock(DnaValidator.class);

    DNAConstraintValidator constraintValidator = new DNAConstraintValidator(mockedValidator);

    ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        constraintValidatorContext = mockConstraintValidatorContext();
    }

    @Test
    void disablesDefaultMessage() {
        String[] invalidDna = {"ATC", "ACG"};

        mockValidatorToReturnErrors(invalidDna, "any error");

        assertFalse(constraintValidator.isValid(invalidDna, constraintValidatorContext));
        // assert that it removes the default message
        verify(constraintValidatorContext, times(1)).disableDefaultConstraintViolation();
    }

    @Test
    void populatesMessagesFromValidator() {
        String[] invalidDna = {"ATC", "ACG"};

        String[] errors = {"error1", "error2", "error3"};
        mockValidatorToReturnErrors(invalidDna, errors);

        assertFalse(constraintValidator.isValid(invalidDna, constraintValidatorContext));
        // assert that it fills the 3 messages
        verify(constraintValidatorContext, times(3)).buildConstraintViolationWithTemplate(anyString());

        for (String error : errors) {
            verify(constraintValidatorContext).buildConstraintViolationWithTemplate(error);
        }
    }

    private ConstraintValidatorContext mockConstraintValidatorContext() {
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderContext =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(builder.addPropertyNode(any())).thenReturn(nodeBuilderContext);
        return constraintValidatorContext;
    }

    void mockValidatorToReturnErrors(String[] dna, String... messages) {
        HashSet<String> errors = new HashSet<>();

        errors.addAll(Arrays.asList(messages));

        when(mockedValidator.checkForErrors(dna)).thenReturn(errors);
    }
}