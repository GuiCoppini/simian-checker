package meli.simian.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimianCheckerServiceTest {

    SimianCheckerService checker = new SimianCheckerService();

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