package meli.simian.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DNACacheCleaner {

    // run every 15 minutes
    @Scheduled(cron = "0 0/15 * * * *")
    @CacheEvict(value = "dna", allEntries = true)
    public void clearCache() {
        log.info("[JOB] Clearing DNA cache");
    }
}