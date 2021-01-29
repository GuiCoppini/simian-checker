package meli.simian.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.model.StatsInfo;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    private DnaRepository repository;

    @Autowired
    public StatsServiceImpl(DnaRepository repository) {
        this.repository = repository;
    }

    // I didn't make this method return StatsResponse because it would force the service to be aware of the
    // REST layer domain, so I return a domain object and let the controller take care of the convertion
    @Override
    public StatsInfo getActualStats() {
        long mutantCount = repository.countByType(DnaType.MUTANT);
        long humanCount = repository.countByType(DnaType.HUMAN);


        StatsInfo statsInfo = StatsInfo.build(mutantCount, humanCount);
        log.info("Statistics calculated, statsInfo={}", statsInfo);
        return statsInfo;
    }
}