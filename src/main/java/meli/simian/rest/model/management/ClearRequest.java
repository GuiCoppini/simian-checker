package meli.simian.rest.model.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearRequest {
    @JsonProperty("clear_cache")
    boolean clearCache;

    @JsonProperty("clear_database")
    boolean clearDatabase;
}
