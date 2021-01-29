package meli.simian.rest.model;

import lombok.Data;
import meli.simian.rest.validation.ValidDNAMatrix;

import javax.validation.constraints.NotNull;

@Data
public class DNACheckRequest {
    @NotNull
    @ValidDNAMatrix
    private String[] dna;
}
