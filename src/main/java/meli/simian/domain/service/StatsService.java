package meli.simian.domain.service;

import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.model.StatsInfo;
import meli.simian.domain.repository.DnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private DnaRepository repository;

    @Autowired
    public StatsService(DnaRepository repository) {
        this.repository = repository;
    }

    // I didn't make this method return StatsResponse because it would force the service to be aware of the
    // REST layer domain, so I return a domain object and let the controller take care of the convertion
    public StatsInfo getActualStats() {
        long mutantCount = repository.countByType(DnaType.MUTANT);
        long humanCount = repository.countByType(DnaType.HUMAN);

        return StatsInfo.build(mutantCount, humanCount);
    }
}