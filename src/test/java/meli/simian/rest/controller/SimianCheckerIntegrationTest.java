package meli.simian.rest.controller;

import com.mongodb.BasicDBObject;
import meli.simian.SimianApplication;
import meli.simian.rest.model.DNACheckRequest;
import meli.simian.rest.model.ErrorPayload;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;


// This test suite will magically build an embedded MongoDB thanks to some dependencies added
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SimianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureDataMongo
class SimianCheckerIntegrationTest {

    @Autowired
    TestRestTemplate client;

    @Autowired
    MongoTemplate mongoTemplate;

    @Before
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
}