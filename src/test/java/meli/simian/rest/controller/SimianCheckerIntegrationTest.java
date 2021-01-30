package meli.simian.rest.controller;

import com.mongodb.BasicDBObject;
import meli.simian.SimianApplication;
import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.domain.service.impl.DnaHasherServiceImpl;
import meli.simian.rest.model.DNACheckRequest;
import meli.simian.rest.model.ErrorPayload;
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

import static org.junit.Assert.assertEquals;


// This test suite will magically build an embedded MongoDB thanks to some dependencies added
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SimianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
class SimianCheckerIntegrationTest {

    @Autowired
    TestRestTemplate client;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DnaRepository repository;

    @BeforeEach
    void clearDb() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (!collectionName.startsWith("system.")) {
                mongoTemplate.getCollection(collectionName).deleteMany(new BasicDBObject());
            }
        }
    }

    @Test
    void humanDnaShouldReturn200() {
        DNACheckRequest req = new DNACheckRequest(new String[] {"ATA", "CGA", "GCA"});

        ResponseEntity<String> stringResponseEntity = client.postForEntity("/simian", req, String.class);

        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
    }


    @Test
    void mutantDnaReturns403() {
        DNACheckRequest req = new DNACheckRequest(new String[] {"AAAA", "CCTA", "GCAA", "AGGT"});

        ResponseEntity<String> stringResponseEntity = client.postForEntity("/simian", req, String.class);

        assertEquals(HttpStatus.FORBIDDEN, stringResponseEntity.getStatusCode());
    }

    @Test
    void notSquareDnaReturns400() {
        DNACheckRequest req = new DNACheckRequest(new String[] {"AAA", "CCTA", "GCAA", "AGGT"});

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(1, body.getErrors().size());
    }

    @Test
    void dnaWithInvalidCharactersReturns400() {
        DNACheckRequest req = new DNACheckRequest(new String[] {"ATEA", "CNTA", "GCAA", "AKKT"});

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(1, body.getErrors().size());

    }

    @Test
    void dnaWithLowerCaseBasesReturns400() {
        DNACheckRequest req = new DNACheckRequest(new String[] {"aa", "aa"});

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(1, body.getErrors().size());
    }

    @Test
    void dnaWithMultipleErrorsReturns400AndErrorList() {
        // making a DNA with invalid characters and not squad, we expect 2 errors on the response
        DNACheckRequest req = new DNACheckRequest(new String[] {"AP", "K"});

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(2, body.getErrors().size());
    }

    @Test
    void emptyDnaMatrix() {
        DNACheckRequest req = new DNACheckRequest(new String[] {});

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorPayload body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(1, body.getErrors().size());
    }

    @Test
    void nullDnaMatrix() {
        DNACheckRequest req = new DNACheckRequest(null);

        ResponseEntity<ErrorPayload> response = client.postForEntity("/simian", req, ErrorPayload.class);
        ErrorPayload body = response.getBody();

        System.out.println(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getMessage());
        assertEquals(1, body.getErrors().size());
    }

    @Test
    void repeatingTheRequestHasTheSameResult() {

        DNACheckRequest request = new DNACheckRequest(new String[]{"AG", "CT"});

        ResponseEntity<ResponseEntity> first = client.postForEntity("/simian", request, ResponseEntity.class);
        ResponseEntity<ResponseEntity> second = client.postForEntity("/simian", request, ResponseEntity.class);

        assertEquals(first, second);
    }

    @Test
    void repeatingRequestWontPersistTwice() {

        // asserting it begins empty
        assertEquals(0, repository.findAll().size());

        DNACheckRequest request = new DNACheckRequest(new String[]{"AG", "CT"});

        client.postForEntity("/simian", request, ResponseEntity.class);
        client.postForEntity("/simian", request, ResponseEntity.class);

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void firstRequestPersistsEntity() {
        // asserting it begins empty
        assertEquals(0, repository.findAll().size());

        DnaHasherServiceImpl hasher = new DnaHasherServiceImpl();

        String[] DNA_SENT = {"AG", "CT"};
        DNACheckRequest request = new DNACheckRequest(DNA_SENT);

        client.postForEntity("/simian", request, ResponseEntity.class);

        DnaDocument expected = new DnaDocument("any id", DnaType.HUMAN, hasher.hash(DNA_SENT), LocalDateTime.now());

        // making sure that the persisted entity had correct type and hash. In other words I'm making sure it persisted
        // the correct entity
        DnaDocument persisted = repository.findAll().get(0);
        assertEquals(expected.getType(), persisted.getType());
        assertEquals(expected.getDnaHash(), persisted.getDnaHash());
    }
}