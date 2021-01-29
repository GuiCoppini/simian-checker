package meli.simian.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsResponse {
    @JsonProperty("count_mutant_dna")
    private Long mutantCount;

    @JsonProperty("count_human_dna")
    private Long humanCount;

    private Double ratio;
}
