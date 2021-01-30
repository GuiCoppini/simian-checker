package meli.simian.domain.model;

import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


// besides being as model class, it has some logic to it, therefore it needs to be tested
class StatsInfoTest {
    @Test
    void buildMethodCalculatesRightRatio() {
        long humans = 123;
        long mutants = 523;

        // I expect it to make exactly THIS calculation
        double expectedRatio = (double) mutants / humans;

        assertEquals(expectedRatio, StatsInfo.build(mutants, humans).getRatio());
    }

    @Test
    void returnsNullRatioIfHumanCountIsZero() {
        long mutantCount  = 50L;
        long humanCount = 0L;

        StatsInfo response = StatsInfo.build(mutantCount, humanCount);

        assertNull(response.getRatio());
    }
}