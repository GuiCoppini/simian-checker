package meli.simian.domain.service;

import meli.simian.domain.entity.DnaDocument;

public interface DnaService {
    DnaDocument persistComputation(String[] dna, boolean isSimian);

    DnaDocument findByDna(String[] dna);
}
