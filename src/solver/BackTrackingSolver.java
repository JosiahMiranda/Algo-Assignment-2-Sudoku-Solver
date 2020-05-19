/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.StdSudokuGrid;
import grid.SudokuGrid;


/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver
{
    // TODO: Add attributes as needed.

    public BackTrackingSolver() {
        // TODO: any initialisation you want to implement.
    } // end of BackTrackingSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        // TODO: your implementation of the backtracking solver for standard Sudoku.
    	
    	StdSudokuGrid stdGrid = (StdSudokuGrid) grid;
    	int size = stdGrid.size;
    	int[][] intGrid = stdGrid.grid;
    	int[] values = stdGrid.values;
    	
    	if (sudokuSolve(intGrid, values, size)) {
    		return true;
    	}

        return false;
    } // end of solve()
    
    public boolean sudokuSolve(int[][] grid, int[] values, int size) {
    	
    	int row = -1;
    	int col = -1;
    	boolean noEmptyRemaining = true;
    	
    	// Search for first row and column that are not empty
    	for (int r = 0; r < size; ++r) {
    		for (int c = 0; c < size; ++c) {
    			if (grid[r][c] == -1) {
    				noEmptyRemaining = false;
    				row = r;
    				col = c;
    				break;
    			}
    		}
    		
    		// early termination
    		if (!noEmptyRemaining) {
    			break;
    		}
    	}
    	
    	// IF there are no empty spots left in the grid then it's solved. Return true.
    	if (noEmptyRemaining) {
    		return true;
    	}
    	
    	// Try each each value in the values array
    	for (int value : values) {
    		if (validMove(grid, row, col, value)) {
    			// Try setting the current row and col to that value
    			grid[row][col] = value;
    			// Run recursive step
    			if (sudokuSolve(grid, values, size)) {
    				return true;
    			} else {
    				// If this is run, then the earlier set value was an invalid move. Set back to -1.
    				grid[row][col] = -1;
    			}
    		}
    	}
    	
    	return false;
    }
    
 	public boolean validMove(int[][] grid, int row, int col, int value) {

 		// Check that the value doesn't already exist in the row
 		int size = grid.length;
 		for (int i = 0; i < size; ++i) {
 			if (grid[row][i] == value) {
 				return false;
 			}
 		}
 		
 		// check that the value doesn't already exist in the column
 		
 		for (int i = 0; i < size; ++i) {
 			if (grid[i][col] == value) {
 				return false;
 			}
 		}
 		
 		
 		// Set up the variables needed for the box check
 		int squareRoot = (int) Math.sqrt(size);
 		int blockRowStartIndex = row - row % squareRoot;
 		int blockColStartIndex = col - col % squareRoot;
 		
 		// Check that the value doesn't already exist in the box
 		for (int r = blockRowStartIndex; r < blockRowStartIndex + squareRoot; ++r) {
 			for (int c = blockColStartIndex; c < blockColStartIndex + squareRoot; ++c) {
 				if (grid[r][c] == value) {
 					return false;
 				}
 			}
 		}
 		
 		// If the program didn't return false, then it returns true as this move is valid
 		return true;
 		
 	}

} // end of class BackTrackingSolver()
