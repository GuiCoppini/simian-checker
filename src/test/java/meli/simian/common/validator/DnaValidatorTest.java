package meli.simian.common.validator;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DnaValidatorTest {

    DnaValidator validator = new DnaValidator();

    @Test
    void notSquareDNAMatrix() {
        String[] invalidDna = {"ATC", "ACG"};
        Set<String> errors = validator.checkForErrors(invalidDna);

        assertEquals(1, errors.size());
    }

    @Test
    void dnaWithLowerCase() {
        String[] invalidDna = {"aA", "TC"};
        Set<String> errors = validator.checkForErrors(invalidDna);

        assertEquals(1, errors.size());
    }

    @Test
    void emptyDNA() {
        String[] invalidDna = {};

        Set<String> errors = validator.checkForErrors(invalidDna);
        assertEquals(1, errors.size());
    }

    @Test
    void invalidCharacters() {
        String[] invalidDna = {"ACT", "PER", "AAA"};

        Set<String> errors = validator.checkForErrors(invalidDna);
        assertEquals(1, errors.size());
    }

    @Test
    void correctDna() {
        String[] validDna = {"ACT", "AGC", "AAA"};

        Set<String> errors = validator.checkForErrors(validDna);
        assertEquals(0, errors.size());
    }

    @Test
    void multipleErrors() {
        String[] validDna = {"AYT", "AC", "A"};

        Set<String> errors = validator.checkForErrors(validDna);
        assertEquals(2, errors.size());
    }
}