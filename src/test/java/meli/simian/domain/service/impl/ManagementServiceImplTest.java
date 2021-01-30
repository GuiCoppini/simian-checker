package meli.simian.domain.service.impl;

import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.ManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagementServiceImplTest {


    CacheManager cacheMock = mock(CacheManager.class);
    Cache mockedCache = mock(Cache.class);

    DnaRepository repoMock = mock(DnaRepository.class);

    ManagementService service = new ManagementServiceImpl(cacheMock, repoMock);

    @Test
    void clearsAllCaches() {
        List<String> caches = List.of("1", "2", "3", "4", "5");

        when(cacheMock.getCacheNames()).thenReturn(caches);
        caches.forEach(cacheName -> {
            when(cacheMock.getCache(cacheName)).thenReturn(mockedCache);
        });

        service.clearCache();

        // not so precise assertion, but I assert that it got the cache of those 5 cacheNames
        caches.forEach(cacheName -> {
            verify(cacheMock, times(1)).getCache(cacheName);
        });

        // and then I verified it cleared the cache gotten 5 times (one for each name)
        verify(mockedCache, times(caches.size())).clear();
    }

    @Test
    void clearsDnaDatabase() {

        service.clearDatabase();

        verify(repoMock, times(1)).deleteAll();
    }

}