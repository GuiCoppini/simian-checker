package meli.simian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"meli.simian.domain.repository"})
@EnableMongoAuditing
public class SimianApplication {

    //TODO use interface for services
    //TODO implement modelmapper (or not)
    public static void main (String[] args) {
        SpringApplication.run(SimianApplication.class, args);
    }

    public static void main2 (String[] args) {
        //		String[] notSim = {"CTGAGA",
        //						"CTGACC",
        //						"TGTTGT",
        //						"AGAGGG",
        //						"CCGCTA",
        //						"TCACTG"};
        //
        //		String[] verticalSim = {"CTGTGA",
        //								"CTGAGC",
        //								"CATTGT",
        //								"CGAGGG",
        //								"GCCCTA",
        //								"TCACTG"};
        //
        //		String[] horizontalSim = {"CCCCGA",
        //				"GTGAGC",
        //				"CATTGT",
        //				"CGAGGG",
        //				"GCCCTA",
        //				"TCACTG"};
        //
        //		String[] diagonalSim = {"CCTCGA",
        //								"GCGAGC",
        //								"CACTAT",
        //								"CGACGG",
        //								"GCCCTA",
        //								"TCACTG"};
        //
        //		System.out.println("Please be false: " + isSimian(notSim));
        //		System.out.println("Please be true (VER): " + isSimian(verticalSim));
        //		System.out.println("Please be true (HOR): " + isSimian(horizontalSim));
        //		System.out.println("Please be true (DIA): " + isSimian(diagonalSim));
    }
}
