package meli.simian.domain.service.impl;

import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.DnaHasherService;
import meli.simian.domain.service.DnaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static meli.simian.domain.entity.enumerator.DnaType.HUMAN;
import static meli.simian.domain.entity.enumerator.DnaType.MUTANT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DnaServiceImplTest {


    DnaHasherService hasher = mock(DnaHasherServiceImpl.class);
    DnaRepository repository = mock(DnaRepository.class);

    DnaService service = new DnaServiceImpl(hasher, repository);

    @Test
    void convertsDnaToDnaHashBeforeSaving() {
        String[] rawDna = {"I'm", "a", "raw", "dna"};
        String hashedDna = "I'm the hashed DNA";
        when(hasher.hash(rawDna)).thenReturn(hashedDna);

        service.persistComputation(rawDna, false);

        ArgumentCaptor<DnaDocument> captor = ArgumentCaptor.forClass(DnaDocument.class);

        verify(hasher, times(1)).hash(rawDna);
        verify(repository, times(1)).save(captor.capture());

        DnaDocument persistedDna = captor.getValue();
        assertEquals(hashedDna, persistedDna.getDnaHash());
    }

    @Test
    void persistCorrectDnaType_simian() {
        String[] rawDna = {"I'm", "a", "raw", "dna"};
        String hashedDna = "anything";
        when(hasher.hash(rawDna)).thenReturn(hashedDna);

        boolean IS_SIMIAN = true;

        service.persistComputation(rawDna, true);

        ArgumentCaptor<DnaDocument> captor = ArgumentCaptor.forClass(DnaDocument.class);

        verify(repository, times(1)).save(captor.capture());

        DnaDocument persistedDna = captor.getValue();
        assertEquals(MUTANT, persistedDna.getType());
    }

    @Test
    void persistCorrectDnaType_notSimian() {
        String[] rawDna = {"I'm", "a", "raw", "dna"};
        String hashedDna = "anything";
        when(hasher.hash(rawDna)).thenReturn(hashedDna);

        service.persistComputation(rawDna, false);

        ArgumentCaptor<DnaDocument> captor = ArgumentCaptor.forClass(DnaDocument.class);

        verify(repository, times(1)).save(captor.capture());

        DnaDocument persistedDna = captor.getValue();
        assertEquals(HUMAN, persistedDna.getType());
    }

    @Test
    void hashesBeforeSearching() {
        String[] rawDna = {"I'm", "a", "raw", "dna"};
        String hashedDna = "anything";
        when(hasher.hash(rawDna)).thenReturn(hashedDna);

        DnaDocument expectedResult = DnaDocument.builder()
                .dnaHash(hashedDna)
                .id("this is the id")
                .type(HUMAN)
                .build();

        when(repository.findByDnaHash(hashedDna)).thenReturn(expectedResult);

        DnaDocument returnedDna = service.findByDna(rawDna);

        verify(repository, times(1)).findByDnaHash(hashedDna);

        // if the returned DNA equals the expected DNA, it means it called the mock with the correct arguments
        // in other words, it actually hasahed the dna before querying
        assertEquals(expectedResult, returnedDna);
    }

}