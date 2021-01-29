package meli.simian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"meli.simian.domain.repository"})
@EnableMongoAuditing
public class SimianApplication {

    //TODO implement modelmapper (or not)
    public static void main (String[] args) {
        SpringApplication.run(SimianApplication.class, args);
    }

}
