package meli.simian.rest.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import meli.simian.rest.validator.ValidDNAMatrix;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DNACheckRequest {

    @ApiModelProperty(value = "The list of each DNA sequence. Has to be a SQUARE matrix containing only the letters" +
            "'A', 'C', 'T' or 'G' on uppercases.",
    example = "[\"AT\", \"CG\"]", dataType = "String[]")
    @ValidDNAMatrix
    private String[] dna;
}
