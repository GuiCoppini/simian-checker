package meli.simian.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import meli.simian.rest.validator.ValidDNAMatrix;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DNACheckRequest {
    @ValidDNAMatrix
    private String[] dna;
}
