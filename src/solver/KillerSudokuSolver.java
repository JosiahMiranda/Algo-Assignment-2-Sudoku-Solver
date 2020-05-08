/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

/**
 * Abstract class for common attributes or methods for solvers of Killer Sudoku.
 * Note it is not necessary to use this, but provided in case you wanted to do
 * so and then no need to change the hierarchy of solver types.
 */
public abstract class KillerSudokuSolver extends SudokuSolver {

	// check that a move is valid
	public boolean validMove(int[][] grid, int row, int col, int value, Cage[] cages) {

		int size = grid.length;
		for (int i = 0; i < size; ++i) {
			if (grid[row][i] == value) {
				return false;
			}
		}

		for (int i = 0; i < size; ++i) {
			if (grid[i][col] == value) {
				return false;
			}
		}

		int squareRoot = (int) Math.sqrt(size);
		int blockRowStartIndex = row - row % squareRoot;
		int blockColStartIndex = col - col % squareRoot;

		for (int r = blockRowStartIndex; r < blockRowStartIndex + squareRoot; ++r) {
			for (int c = blockColStartIndex; c < blockColStartIndex + squareRoot; ++c) {
				if (grid[r][c] == value) {
					return false;
				}
			}
		}

		Cage cage = findCage(row, col, cages);
		
		if (cage == null) {
			return false;
		} else {
			Tuple[] tuples = cage.tuples;
			int numEmpties = 0;
			int total = 0;
			for (Tuple tuple : tuples) {
				if (grid[tuple.row][tuple.col] == -1) {
					++numEmpties;
				} else {
					total += grid[tuple.row][tuple.col];
				}
			}
			
			if (numEmpties == 1) {
				if ((total + value) != cage.sum) {
					return false;
				}
			} else {
				if ((total + value) > cage.sum) {
					return false;
				}
			}
		}
		
		return true;

	}

	private Cage findCage(int row, int col, Cage[] cages) {
		for (Cage cage : cages) {
			for (Tuple tuple : cage.tuples) {
				if (row == tuple.row && col == tuple.col) {
					return cage;
				}
			}
		}
		
		return null;
	}

} // end of class KillerSudokuSolver
