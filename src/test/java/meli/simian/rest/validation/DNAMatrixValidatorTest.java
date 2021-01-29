package meli.simian.rest.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DNAMatrixValidatorTest {

    DNAMatrixValidator validator = new DNAMatrixValidator();

    ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        constraintValidatorContext = mockConstraintValidatorContext();

    }

    @Test
    void disablesDefaultMessage() {
        String[] invalidDna = {"ATC", "ACG"};

        assertFalse(validator.isValid(invalidDna, constraintValidatorContext));
        // assert that it removes the default message
        verify(constraintValidatorContext, times(1)).disableDefaultConstraintViolation();
    }

    @Test
    void notSquareDNAMatrix() {
        String[] invalidDna = {"ATC", "ACG"};

        assertFalse(validator.isValid(invalidDna, constraintValidatorContext));
        // assert that it adds an error message to the constraintValidatorContext
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(any(String.class));
    }

    @Test
    void dnaWithLowerCase() {
        String[] invalidDna = {"aA", "TC"};

        assertFalse(validator.isValid(invalidDna, constraintValidatorContext));
        // assert that it adds an error message to the constraintValidatorContext
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(any(String.class));
    }

    @Test
    void emptyDNA() {
        String[] invalidDna = {};

        assertFalse(validator.isValid(invalidDna, constraintValidatorContext));
    }

    @Test
    void invalidCharacters() {
        String[] invalidDna = {"ACT", "PER"};

        assertFalse(validator.isValid(invalidDna, constraintValidatorContext));
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
}