package meli.simian.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import meli.simian.SimianApplication;
import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.rest.model.StatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SimianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
class StatsIntegrationTest {

    @Autowired
    TestRestTemplate client;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DnaRepository repository;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void clearDb() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (!collectionName.startsWith("system.")) {
                mongoTemplate.getCollection(collectionName).deleteMany(new BasicDBObject());
            }
        }
    }

    @Test
    void getsCorrentHumanCount() {
        long humanCount = 333;
        populateDatabase(humanCount, 555);

        ResponseEntity<StatsResponse> response = client.getForEntity("/stats", StatsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(humanCount, response.getBody().getHumanCount());
    }


    @Test
    void getsCorrentMutantCount() {
        long mutantCount = 555;
        populateDatabase(333, mutantCount);

        ResponseEntity<StatsResponse> response = client.getForEntity("/stats", StatsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mutantCount, response.getBody().getMutantCount());
    }

    @Test
    void calculatesCorrectRatio_moreHumans() {
        long humans = 841;
        long mutants = 564;

        double expectedRatio = 0.67;

        populateDatabase(humans, mutants);

        // correct is mutant/human
        ResponseEntity<StatsResponse> response = client.getForEntity("/stats", StatsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRatio, response.getBody().getRatio());
    }

    @Test
    void calculatesCorrectRatio_moreMutants() {
        long humans = 158;
        long mutants = 654;

        double expectedRatio = 4.14;
        populateDatabase(humans, mutants);

        // correct is mutant/human
        ResponseEntity<StatsResponse> response = client.getForEntity("/stats", StatsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRatio, response.getBody().getRatio());
    }

    @Test
    public void doesNotShowRatioFieldWhenHumanCountIsZero()
            throws JsonProcessingException {
        long humans = 0;
        long mutants = 654;

        populateDatabase(humans, mutants);

        // correct is mutant/human
        ResponseEntity<StatsResponse> response = client.getForEntity("/stats", StatsResponse.class);

        String json = mapper.writeValueAsString(response);

        assertThat(json, containsString("count_mutant_dna"));
        assertThat(json, containsString("count_human_dna"));
        assertThat(json, not(containsString("ratio")));
    }


    private void populateDatabase(long humans, long mutants) {
        for (int i = 0; i < humans; i++) {
            String uniqueString = i + "-HUMAN";
            DnaDocument entity = DnaDocument.builder()
                    .id(uniqueString)
                    .type(DnaType.HUMAN)
                    .dnaHash(uniqueString)
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(entity);
        }

        for (int i = 0; i < mutants; i++) {
            String uniqueString = i + "-MUTANT";
            DnaDocument entity = DnaDocument.builder()
                    .id(uniqueString)
                    .type(DnaType.MUTANT)
                    .dnaHash(uniqueString)
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(entity);
        }
    }

}