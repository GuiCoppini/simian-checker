package meli.simian.domain.entity;

import lombok.Builder;
import lombok.Data;
import meli.simian.domain.entity.enumerator.DnaType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "dna")
@Builder
public class DnaDocument {
    @MongoId
    String id;

    @Indexed
    DnaType type;

    @Indexed(unique = true)
    String dnaHash;

    @CreatedDate
    LocalDateTime createdAt;

    public boolean isSimian() {
        return type == DnaType.MUTANT;
    }
}
