package meli.simian.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    @JsonProperty("count_mutant_dna")
    private Long mutantCount;

    @JsonProperty("count_human_dna")
    private Long humanCount;

    private String ratio;
}
