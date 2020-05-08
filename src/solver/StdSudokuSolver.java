/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

/**
 * Abstract class for common attributes or methods for solvers of standard
 * Sudoku. Note it is not necessary to use this, but provided in case you wanted
 * to do so and then no need to change the hierarchy of solver types.
 */
public abstract class StdSudokuSolver extends SudokuSolver {

	// check that a move is valid
	public boolean validMove(int[][] grid, int row, int col, int value) {

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
		
		
		return true;
		
	}

} // end of class StdSudokuSolver
