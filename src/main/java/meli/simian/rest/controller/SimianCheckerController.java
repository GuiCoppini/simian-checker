package meli.simian.rest.controller;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.service.OrchestratorService;
import meli.simian.rest.model.DNACheckRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class SimianCheckerController {

    // autowiring via constructor allows increased flexibility for mocks and tests
    private OrchestratorService service;

    @Autowired
    public SimianCheckerController (OrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/simian")
    public ResponseEntity dnaCheck(@Valid @RequestBody DNACheckRequest dna) {
        log.info("Checking for simian pattern on DNA={}",dna);
        boolean isSimian = service.process(dna.getDna());

        log.info("DNA computed successfully, isSimian={}", isSimian);
        if(isSimian) return new ResponseEntity(HttpStatus.FORBIDDEN);
        return new ResponseEntity(HttpStatus.OK);
    }

}
