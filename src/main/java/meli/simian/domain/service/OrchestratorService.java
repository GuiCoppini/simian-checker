package meli.simian.domain.service;

import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrchestratorService {

    DnaRepository repository;
    DnaHasherService hasher;
    SimianCheckerService simianCheckerService;

    @Autowired
    public OrchestratorService(DnaRepository repository, DnaHasherService hasher, SimianCheckerService simianCheckerService) {
        this.simianCheckerService = simianCheckerService;
        this.repository = repository;
        this.hasher = hasher;
    }

    public boolean process(String[] dna) {
        String hash = hasher.hash(dna);
        DnaDocument storedDna = repository.findByDnaHash(hash);

        // being null means it has never been computated
        if(storedDna == null) {
            boolean isSimian = simianCheckerService.isSimian(dna);
            DnaDocument dnaEntity = DnaDocument.builder()
                    .type(isSimian ? DnaType.MUTANT : DnaType.HUMAN)
                    .dnaHash(hash)
                    .build();

            repository.save(dnaEntity);
            return isSimian;
        } else {
            return storedDna.isSimian();
        }
    }

}


