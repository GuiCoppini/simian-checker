package meli.simian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"meli.simian.domain.repository"})
@EnableMongoAuditing
@EnableSwagger2
@EnableCaching
@EnableScheduling
public class SimianApplication {
    public static void main (String[] args) {
        SpringApplication.run(SimianApplication.class, args);
    }
}
