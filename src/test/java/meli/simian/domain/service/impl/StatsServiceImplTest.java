package meli.simian.domain.service.impl;

import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.model.StatsInfo;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.StatsService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatsServiceImplTest {
    DnaRepository repo = mock(DnaRepository.class);

    StatsService service = new StatsServiceImpl(repo);

    @Test
    void usesRepositoryToBuildResponse() {
        long mutantCount  = 50L;
        long humanCount = 100L;
        // it was precalculated to ensure it will come out on the format I programmed to
        double preCalculatedRatio = 0.5;

        StatsInfo expectedResult = new StatsInfo(mutantCount, humanCount, preCalculatedRatio);

        mockRepositories(mutantCount, humanCount);

        StatsInfo response = service.getActualStats();

        assertEquals(expectedResult, response);
        verify(repo, times(1)).countByType(DnaType.HUMAN);
        verify(repo, times(1)).countByType(DnaType.MUTANT);
    }

    private void mockRepositories(long mutantCount, long humanCount) {
        when(repo.countByType(DnaType.HUMAN)).thenReturn(
                humanCount
        );

        when(repo.countByType(DnaType.MUTANT)).thenReturn(
                mutantCount
        );
    }

}