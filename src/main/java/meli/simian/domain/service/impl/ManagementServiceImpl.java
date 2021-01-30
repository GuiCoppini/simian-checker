package meli.simian.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.ManagementService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {

    CacheManager cacheManager;
    DnaRepository dnaRepository;

    public ManagementServiceImpl(CacheManager cacheManager, DnaRepository dnaRepository) {
        this.cacheManager = cacheManager;
        this.dnaRepository = dnaRepository;
    }

    @Override
    public void clearDatabase() {
        log.info("Deleting DNA documents");
        dnaRepository.deleteAll();
        log.info("DNA documents deleted successfully");
    }

    @Override
    public void clearCache() {
        log.info("Clearing all caches");
        cacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        log.info("Caches cleared successfully");
    }
}
