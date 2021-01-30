package meli.simian.rest.controller;

import meli.simian.SimianApplication;
import meli.simian.TestUtils;
import meli.simian.domain.entity.DnaDocument;
import meli.simian.domain.entity.enumerator.DnaType;
import meli.simian.domain.repository.DnaRepository;
import meli.simian.rest.model.management.ClearRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


// This test suite will magically build an embedded MongoDB thanks to some dependencies added
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SimianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
class ManagementIntegrationTest {

    @Autowired
    TestRestTemplate client;

    @Autowired
    DnaRepository repository;

    @Autowired
    TestUtils testUtils;

    @BeforeEach
    public void cleanupEnvironment() {
        testUtils.cleanupEnvironment();
    }

    @Test
    void databaseClearing() {
        ClearRequest request = new ClearRequest(false, true);

        repository.save(new DnaDocument("id", DnaType.HUMAN, "anyhash", LocalDateTime.now()));
        repository.save(new DnaDocument("id2", DnaType.HUMAN, "anyhash2", LocalDateTime.now()));

        assertEquals(2, repository.findAll().size());
        client.put("/mgmt/clear", request);

        // assert it cleared the DB
        assertEquals(0, repository.findAll().size());
    }
}