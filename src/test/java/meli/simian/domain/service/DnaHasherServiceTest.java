package meli.simian.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DnaHasherServiceTest {

    DnaHasherService hasher = new DnaHasherService();

    @Test
    void hashingSameArrayTwice() {
        String[] willBeHashedTwice = {"Help!", "I", "will", "be", "hashed", "twice"};

        String hash1 = hasher.hash(willBeHashedTwice);
        String hash2 = hasher.hash(willBeHashedTwice);

        assertEquals(hash1, hash2);
    }

    @Test
    void hashingCloseButDifferentArrays() {
        // this test will ensure arrays like {"A", "A", "A", "A"} and {"AA", "AA"} wont be equal on the database
        String[] firstArray = {"A","A","A","A"};
        String[] secondArray = {"AA", "AA"};

        String hash1 = hasher.hash(firstArray);
        String hash2 = hasher.hash(secondArray);

        assertNotEquals(hash1, hash2);
    }

}