package meli.simian.rest.controller;

import meli.simian.SimianApplication;
import meli.simian.rest.model.DNACheckRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SimianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureDataMongo
class SimianCheckerControllerTest {

    @Autowired
    TestRestTemplate client;


    @Test
    void qqrCoisa() {
        DNACheckRequest req = new DNACheckRequest();
        req.setDna(new String[] {"AAAA", "AAAA", "AAAA", "AAAA"});

        System.out.println("Before " + client.getForEntity("/stats", String.class).getBody());
        ResponseEntity<String> stringResponseEntity = client.postForEntity("/simian", req, String.class);

        System.out.println("After " + client.getForEntity("/stats", String.class).getBody());
        System.out.println(stringResponseEntity);
    }
}