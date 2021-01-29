package meli.simian.domain.service;

import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrchestratorServiceTest {

    DnaRepository repository = mock(DnaRepository.class);
    DnaHasherService hasher = mock(DnaHasherService.class);
    SimianCheckerService simianCheckerService = mock(SimianCheckerService.class);

    OrchestratorService orchestrator = new OrchestratorService(repository, hasher, simianCheckerService);

    @Test
    void alreadyExistentDna() {
        String[] dna = {"literally", "anything"};
        String EXISTENT_HASH = "ExistentHash";
        when(hasher.hash(any())).thenReturn(EXISTENT_HASH);
        when(repository.findByDnaHash(EXISTENT_HASH)).thenReturn(createDnaDocument(DnaType.HUMAN, EXISTENT_HASH));

        boolean result = orchestrator.process(dna);

        // never saved anything
        verify(repository, never()).save(any());
        // didn't compute anything
        verify(simianCheckerService, never()).isSimian(any());

        // as I mocked the repository to return a HUMAN, I expect that the service return is based on it
        assertFalse(result);
    }

    @Test
    void neverComputedDna() {
        String[] dna = {"literally", "anything"};
        String NON_EXISTENT_HASH = "NonExistentHash";
        when(hasher.hash(any())).thenReturn(NON_EXISTENT_HASH);
        when(repository.findByDnaHash(NON_EXISTENT_HASH)).thenReturn(null);
        when(simianCheckerService.isSimian(dna)).thenReturn(true);

        DnaDocument expectedToBeSaved = createDnaDocument(DnaType.MUTANT, NON_EXISTENT_HASH);

        boolean result = orchestrator.process(dna);

        // captor will check the Dna that was saved
        ArgumentCaptor<DnaDocument> captor = ArgumentCaptor.forClass(DnaDocument.class);

        // saved a mutant dna (as I forced the simianCheckerService to return true)
        verify(repository, times(1)).save(captor.capture());
        DnaDocument savedDnaDocument = captor.getValue();
        assertEquals(expectedToBeSaved.getType(), savedDnaDocument.getType());
        assertEquals(expectedToBeSaved.getDnaHash(), savedDnaDocument.getDnaHash());

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