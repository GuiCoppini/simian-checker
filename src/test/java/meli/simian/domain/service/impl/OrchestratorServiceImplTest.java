package meli.simian.domain.service.impl;


import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.service.DnaService;
import meli.simian.domain.service.OrchestratorService;
import meli.simian.domain.service.SimianCheckerService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrchestratorServiceImplTest {

    DnaService dnaService = mock(DnaServiceImpl.class);
    SimianCheckerService simianCheckerService = mock(SimianCheckerServiceImpl.class);

    OrchestratorService orchestrator = new OrchestratorServiceImpl(dnaService, simianCheckerService);

    @Test
    void alreadyExistentDna() {
        String[] dna = {"literally", "anything"};

        when(dnaService.findByDna(dna)).thenReturn(createDnaDocument(DnaType.HUMAN, "any hash"));

        boolean result = orchestrator.process(dna);

        // never saved anything
        verify(dnaService, never()).persistComputation(any(String[].class), any(boolean.class));
        // didn't compute anything
        verify(simianCheckerService, never()).isSimian(any());

        // as I mocked the repository to return a HUMAN, I expect that the service return is based on it
        assertFalse(result);
    }

    @Test
    void neverComputedDna() {
        String[] dna = {"literally", "anything"};
        when(dnaService.findByDna(dna)).thenReturn(null);
        when(simianCheckerService.isSimian(dna)).thenReturn(true);

        boolean result = orchestrator.process(dna);

        // saved a mutant dna (as I forced the simianCheckerService to return true)
        verify(dnaService, times(1)).persistComputation(dna, result);

        // computed the dna once through the checker
        verify(simianCheckerService, times(1)).isSimian(dna);

        // as I mocked the simianCheckerService to return true, I expect that the service return is based on it
        assertTrue(result);
    }

    DnaDocument createDnaDocument(DnaType type, String dnaHash) {
        return DnaDocument.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .dnaHash(dnaHash)
                .createdAt(LocalDateTime.now())
                .build();
    }
}