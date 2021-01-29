package meli.simian.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import meli.simian.common.validator.DnaValidator;
import meli.simian.domain.service.SimianCheckerService;
import meli.simian.domain.service.enumerator.Direction;
import org.springframework.stereotype.Service;

import static meli.simian.domain.service.enumerator.Direction.LEFT;
import static meli.simian.domain.service.enumerator.Direction.RIGHT;

// this is where the real and heavy algorithm happens, I fully extracted the main logic to this class
// and it does this and nothing else
@Service
@Slf4j
public class SimianCheckerServiceImpl implements SimianCheckerService {
    private final int MAXIMUM_REPETITIONS = 4;

    private DnaValidator validator;

    public SimianCheckerServiceImpl(DnaValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isSimian(String[] dna) {
        // validating before the algorithm because the service layer is agnostic of what the
        // rest layer does, so it must validate the input to avoid an exception during the computation
        validator.validate(dna);

        log.info("Valid DNA sequence, proceeding to the algorithm");
        char[][] matrix = toMatrix(dna);

        return hasHorizontal(matrix) || hasVertical(matrix) || hasDiagonal(matrix);
    }

    private char[][] toMatrix(String[] dna) {
        int matrixOrder = dna[0].length();
        char[][] matrix = new char[matrixOrder][matrixOrder];

        for (int line = 0; line < dna.length; line++) {
            for (int column = 0; column < matrixOrder; column++) {
                matrix[line][column] = dna[line].charAt(column);
            }
        }

        return matrix;
    }

    private boolean hasHorizontal(char[][] dna) {
        log.info("Checking if DNA has horizontal pattern");
        // stores the order of the matrix so I don't need to call .length() on every string
        int matrixOrder = dna.length;
        for (int row = 0; row < matrixOrder; row++) {

            // stores the repetition of a character until it hits 4
            int charCount = 1;
            char actualChar = dna[row][0];
            for (int column = 1; column < matrixOrder; column++) {
                if (actualChar == dna[row][column]) {
                    charCount++;
                    if (charCount == MAXIMUM_REPETITIONS){
                        log.info("Encountered horizontal pattern in DNA, isSimian=true");
                        return true;
                    }
                } else {
                    charCount = 0;
                    actualChar = dna[row][column];
                }
            }
        }

        return false;
    }

    private boolean hasVertical(char[][] dna) {
        log.info("Checking if DNA has vertical pattern");
        // stores the order of the matrix so I don't need to call .length() on every string
        int matrixOrder = dna.length;
        for (int column = 0; column < matrixOrder; column++) {

            int charCount = 1;
            char actualChar = dna[0][column];

            for (int row = 1; row < matrixOrder; row++) {
                if (actualChar == dna[row][column]) {
                    charCount++;
                    if (charCount == MAXIMUM_REPETITIONS){
                        log.info("Encountered vertical pattern in DNA, isSimian=true");
                        return true;
                    }
                } else {
                    charCount = 0;
                    actualChar = dna[row][column];
                }
            }
        }

        return false;
    }

    // unfortunately recursive
    private boolean hasDiagonal(char[][] dna) {
        log.info("Checking if DNA has diagonal pattern");
        for (int row = 0; row < dna.length; row++) {
            for (int column = 0; column < dna.length; column++) {
                char actualChar = dna[row][column];

                // saving it to a boolean to avoid calling the recursive method twice
                boolean result = checkDiagonals(dna, actualChar, row, column);
                if (result){
                    log.info("Encountered diagonal pattern in DNA, isSimian=true");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkDiagonals(char[][] matrix, char actualChar, int row, int column) {
        return checkLowerNeighborsRecursive(matrix, actualChar, row, column, LEFT, 1)
                || checkLowerNeighborsRecursive(matrix, actualChar, row, column, RIGHT, 1);
    }

    private boolean checkLowerNeighborsRecursive(char[][] matrix, char actualChar, int row, int column, Direction dir, int count) {
        if (count == MAXIMUM_REPETITIONS) return true;

        int neighborColumn;
        int neighborRow;
        if (dir == LEFT) {
            neighborColumn = column - 1;
        } else {
            neighborColumn = column + 1;
        }
        // goes down one row
        neighborRow = row + 1;
        // quick check to see if not out of bounds and then check if char is the same
        if (isInBound(dir, matrix.length, neighborRow, neighborColumn)) {
            if(matrix[neighborRow][neighborColumn] == actualChar) {
                return checkLowerNeighborsRecursive(matrix, actualChar, neighborRow, neighborColumn, dir, count + 1);
            }
        }
        return false;
    }

    private boolean isInBound(Direction dir, int matrixOrder, int row, int column) {
        return (dir == RIGHT && row < matrixOrder && column < matrixOrder)
                || (dir == LEFT && row < matrixOrder && column >= 0);
    }
}


