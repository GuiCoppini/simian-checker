package meli.simian.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.service.DnaService;
import meli.simian.domain.service.OrchestratorService;
import meli.simian.domain.service.SimianCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrchestratorServiceImpl implements OrchestratorService {

    DnaService dnaService;
    SimianCheckerService simianCheckerService;

    @Autowired
    public OrchestratorServiceImpl(DnaService dnaService, SimianCheckerService simianCheckerService) {
        this.simianCheckerService = simianCheckerService;
        this.dnaService = dnaService;
    }


    @Cacheable(value = "dna")
    @Override
    public boolean process(String[] dna) {
        log.info("Checking for DNA in database");
        DnaDocument storedDna = dnaService.findByDna(dna);

        // being null means it has never been computated
        if(storedDna == null) {
            log.info("DNA not found in database, checking for mutant patterns in DNA");
            boolean isSimian = simianCheckerService.isSimian(dna);

            log.info("Calling DNASerice to persist computation, isSimian={}, dna={}", isSimian, dna);
            dnaService.persistComputation(dna, isSimian);
            return isSimian;
        } else {
            log.info("DNA found in database, returning stored DNA information. storedDna={}", storedDna);
            return storedDna.isSimian();
        }
    }

}


