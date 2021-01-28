package meli.simios;

import static meli.simios.SimiosApplication.Direction.LEFT;
import static meli.simios.SimiosApplication.Direction.RIGHT;

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

		String[] verticalSim = {"CTGTGA",
								"CTGAGC",
								"CATTGT",
								"CGAGGG",
								"GCCCTA",
								"TCACTG"};

		String[] horizontalSim = {"CCCCGA",
				"GTGAGC",
				"CATTGT",
				"CGAGGG",
				"GCCCTA",
				"TCACTG"};

		String[] diagonalSim = {"CCTCGA",
								"GCGAGC",
								"CACTAT",
								"CGACGG",
								"GCCCTA",
								"TCACTG"};

		System.out.println("Please be false: " + isSimian(notSim));
		System.out.println("Please be true (VER): " + isSimian(verticalSim));
		System.out.println("Please be true (HOR): " + isSimian(horizontalSim));
		System.out.println("Please be true (DIA): " + isSimian(diagonalSim));
	}



	static boolean isSimian (String[] dna) {
		char[][] matrix = toMatrix(dna);

		return hasHorizontal(matrix) || hasVertical(matrix) || hasDiagonal(matrix);
	}

	private static char[][] toMatrix(String[] dna) {
		int matrixOrder = dna[0].length();
		char[][] matrix = new char[matrixOrder][matrixOrder];

		for (int line = 0; line < dna.length; line++) {
			for (int column = 0; column < matrixOrder; column++) {
				matrix[line][column] = dna[line].charAt(column);
			}
		}

		return matrix;
	}

	private static boolean hasHorizontal(char[][] dna) {
		// stores the order of the matrix so I don't need to call .length() on every string
		int matrixOrder = dna.length;
		for (int row = 0; row < matrixOrder; row++) {

			// stores the repetition of a character until it hits 4
			int charCount = 1;
			char actualChar = dna[row][0];
			for (int column = 1; column < matrixOrder; column++) {
				if(actualChar == dna[row][column]) {
					charCount++;
					if(charCount == 4) return true;
				} else {
					charCount = 0;
					actualChar = dna[row][column];
				}
			}
		}

		return false;
	}

	private static boolean hasVertical(char[][] dna) {
		// stores the order of the matrix so I don't need to call .length() on every string
		int matrixOrder = dna.length;
		for (int column = 0; column < matrixOrder; column++) {

			int charCount = 1;
			char actualChar = dna[0][column];

			for (int row = 1; row < matrixOrder; row++) {
				if(actualChar == dna[row][column]) {
					charCount++;
					if(charCount == MAXIMUM_REPETITIONS) return true;
				} else {
					charCount = 0;
					actualChar = dna[row][column];
				}
			}
		}

		return false;
	}


	// unfortunately recursive
	private static boolean hasDiagonal(char[][] dna) {
		for (int row = 0; row < dna.length; row++) {
			for (int column = 0; column < dna.length; column++) {
				char actualChar = dna[row][column];

				// saving it to a boolean to avoid calling the recursive method twice
				boolean result = checkDiagonals(dna, actualChar, row, column);
				if(result) return true;
			}
		}

		return false;
	}

	private static boolean checkDiagonals(char[][] matrix, char actualChar, int row, int column) {
		return checkLowerNeighborsRecursive(matrix, actualChar, row, column, LEFT, 1)
				|| checkLowerNeighborsRecursive(matrix, actualChar, row, column, RIGHT, 1);
	}

	private static boolean checkLowerNeighborsRecursive (char[][] matrix, char actualChar, int row, int column, Direction dir, int count) {
		if(count == 4) return true;

		int neighborColumn ;
		int neighborRow;
		if(dir == LEFT) {
			neighborColumn = column - 1;
			neighborRow = row + 1;
		} else {
			neighborColumn = column + 1;
			neighborRow = row - 1;
		}
		// quick check to see if not out of bounds
		if(neighborRow < matrix.length && neighborColumn >= 0) {
			return checkLowerNeighborsRecursive(matrix, actualChar, neighborRow, neighborColumn, dir, count+1);
		}

		return false;
	}

	enum Direction {
		LEFT, RIGHT;
	}
}
