package meli.simian.domain.repository;

import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DnaRepository extends MongoRepository<DnaDocument, String> {

    Long countByType(DnaType lastname);

    DnaDocument findByDnaHash(String dnaHash);
}
