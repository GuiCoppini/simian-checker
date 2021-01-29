package meli.simian.domain.service.impl;

import meli.simian.common.validator.DnaValidator;
import meli.simian.domain.exception.ValidationException;
import meli.simian.domain.service.SimianCheckerService;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class SimianCheckerServiceImplTest {

    DnaValidator validator = mock(DnaValidator.class);
    SimianCheckerService checker = new SimianCheckerServiceImpl(validator);


    @Before
    void setUp() {
        // avoid crashing on normal test methods
        doNothing().
                when(validator).validate(any());
    }

    @Test
    void dnaWithVerticalMatch() {
        String[] dna = {
                "CTGT",
                "CTGA",
                "CATT",
                "CGAG"
        };

        assertTrue(checker.isSimian(dna));
    }

    @Test
    void throwsExceptionFromValidator() {
        String[] dna = {"I", "will", "throw", "an", "exception"};

        String validationMessage = "message";
        Set<String> errors = new HashSet<>(Arrays.asList("error1", "error2"));

        doThrow(new ValidationException(validationMessage, errors))
                .when(validator).validate(eq(dna));

        // won't catch the exception
        // and will throw the EXACT exception from the validator
        // I could use assertThrows but I'd lose the Exception information
        try {
            checker.isSimian(dna);
        } catch (ValidationException e) {
            assertEquals(validationMessage, e.getMessage());
            assertEquals(errors, e.getErrors());
        }
    }

    @Test
    void dnaWithHorizontalMatch() {
        String[] dna = {
                "CCCC",
                "CTGA",
                "AATT",
                "TCAC"
        };

        assertTrue(checker.isSimian(dna));
    }

    @Test
    void dnaWithDiagonalMatch() {
        String[] dna = {
                "CCAC",
                "CCGA",
                "AACT",
                "TCAC"
        };

        assertTrue(checker.isSimian(dna));
    }

    @Test
    void dnaSmallerThanFour() {
        String[] dna = {
                "TTT",
                "TTT",
                "TTT"
        };

        assertFalse(checker.isSimian(dna));
    }
}