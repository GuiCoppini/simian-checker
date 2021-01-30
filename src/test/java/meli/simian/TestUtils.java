package meli.simian;

import com.mongodb.BasicDBObject;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    CacheManager cacheManager;
    MongoTemplate mongoTemplate;

    public TestUtils(CacheManager cacheManager, MongoTemplate mongoTemplate) {
        this.cacheManager = cacheManager;
        this.mongoTemplate = mongoTemplate;
    }

    public void cleanupEnvironment() {
        // clearing caches
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());

        // clearing database
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (collectionName.startsWith("dna")) {
                mongoTemplate.getCollection(collectionName).deleteMany(new BasicDBObject());
            }
        }
    }
}
