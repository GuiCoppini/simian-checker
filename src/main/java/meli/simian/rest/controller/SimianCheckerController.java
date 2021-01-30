package meli.simian.rest.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.service.OrchestratorService;
import meli.simian.rest.model.DNACheckRequest;
import meli.simian.rest.model.ErrorPayload;
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
    @ApiOperation(value = "Checks sent DNA to confirm if it is simian or not")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The DNA tested was NOT simian; in other words, it was a Human DNA."),
            @ApiResponse(code = 403, message = "The DNA tested was simian; in other words, it was a Mutant DNA."),
            @ApiResponse(code = 400, message = "The request sent was invalid.", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. If this happens, contact the application administrator") })

    public ResponseEntity dnaCheck(@Valid @RequestBody DNACheckRequest dna) {
        log.info("Checking for simian pattern on DNA={}",dna);
        boolean isSimian = service.process(dna.getDna());

        log.info("DNA computed successfully, isSimian={}", isSimian);
        if(isSimian) return new ResponseEntity(HttpStatus.FORBIDDEN);
        return new ResponseEntity(HttpStatus.OK);
    }
}
