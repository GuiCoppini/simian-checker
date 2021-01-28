package meli.simios;

//@SpringBootApplication
public class SimiosApplication {

	public static final int MAXIMUM_REPETITIONS = 4;

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

		String[] sim = {"CTGAGA",
						"CTGAGC",
						"CATTGT",
						"CGAGGG",
						"GCCCTA",
						"TCACTG"};

		System.out.println(isSimian(notSim));
		System.out.println(isSimian(sim));
	}



	static boolean isSimian (String[] dna) {
		return hasHorizontal(dna) || hasVertical(dna);
	}

	private static boolean hasHorizontal(String[] dna) {
		// stores the order of the matrix so I don't need to call .length() on every string
		int matrixOrder = dna[0].length();
		for (String row: dna) {

			// stores the repetition of a character until it hits 4
			int charCount = 1;
			char actualChar = row.charAt(0);
			for (int i = 1; i < matrixOrder; i++) {
				if(actualChar == row.charAt(i)) {
					charCount++;
					if(charCount == 4) return true;
				} else {
					charCount = 0;
					actualChar = row.charAt(i);
				}
			}
		}

		return false;
	}

	// could have done a "rotate matrix and then use hasVertical() method" but rotating a matrix is expensive depending
	// on its size
	private static boolean hasVertical(String[] dna) {
		// stores the order of the matrix so I don't need to call .length() on every string
		int matrixOrder = dna[0].length();

		for (int column = 0; column < matrixOrder; column++) {
			int charCount = 1;
			char actualChar = dna[0].charAt(column);

			for (int row = 1; row < matrixOrder; row++) {
				if(actualChar == dna[row].charAt(column)) {
					charCount++;
					if(charCount == MAXIMUM_REPETITIONS) return true;
				} else {
					charCount = 0;
					actualChar = dna[row].charAt(column);
				}
			}
		}

		return false;
	}

	private static boolean hasDiagonal(String[] dna) {
		return false;
	}

}
