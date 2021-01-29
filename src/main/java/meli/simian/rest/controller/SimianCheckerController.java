package meli.simian.rest.controller;

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
public class SimianCheckerController {

    // autowiring via constructor allows increased flexibility for mocks and tests
    private OrchestratorService service;

    @Autowired
    public SimianCheckerController (OrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/simian")
    public ResponseEntity dnaCheck(@Valid @RequestBody DNACheckRequest dna) {
        boolean isSimian = service.process(dna.getDna());

        if(isSimian) return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

}
