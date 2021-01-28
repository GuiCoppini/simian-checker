package meli.simios;

//@SpringBootApplication
public class SimiosApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(SimiosApplication.class, args);
//	}

	public static void main (String[] args) {
		String[] notSim = {"CTGAGA",
						"CTGACC",
						"TGTTGT",
						"AGAGGG",
						"CCGCTA",
						"TCACTG"};

		String[] sim = {"CTGAGA", "CTGAGC", "TATTGT", "AGAGGG", "CCCCTA", "TCACTG"};

		System.out.println(isSimian(notSim));
		System.out.println(isSimian(sim));
	}



	static boolean isSimian (String[] dna) {

	}

}
