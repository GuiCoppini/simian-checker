package meli.simian.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.DnaHasherService;
import meli.simian.domain.service.DnaService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DnaServiceImpl implements DnaService {

    private DnaHasherService hasher ;
    private DnaRepository repository;

    public DnaServiceImpl(DnaHasherService hasher, DnaRepository repository) {
        this.repository = repository;
        this.hasher = hasher;
    }

    @Override
    public DnaDocument persistComputation(String[] dna, boolean isSimian) {
        String hashedDna = hasher.hash(dna);

        DnaDocument dnaEntity = DnaDocument.builder()
                .type(isSimian ? DnaType.MUTANT : DnaType.HUMAN)
                .dnaHash(hashedDna)
                .build();

        log.info("Persisting DNA, dnaDocument={}", dnaEntity);

        return repository.save(dnaEntity);
    }

    @Override
    public DnaDocument findByDna(String[] dna) {
        String hash = hasher.hash(dna);
        return repository.findByDnaHash(hash);
    }
}
