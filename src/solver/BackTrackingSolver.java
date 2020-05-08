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
    	
    	for (int r = 0; r < size; ++r) {
    		for (int c = 0; c < size; ++c) {
    			if (grid[r][c] == -1) {
    				noEmptyRemaining = false;
    				row = r;
    				col = c;
    				break;
    			}
    		}
    		
    		if (!noEmptyRemaining) {
    			break;
    		}
    	}
    	
    	if (noEmptyRemaining) {
    		return true;
    	}
    	
    	for (int value : values) {
    		if (validMove(grid, row, col, value)) {
    			grid[row][col] = value;
    			if (sudokuSolve(grid, values, size)) {
    				return true;
    			} else {
    				grid[row][col] = -1;
    			}
    		}
    	}
    	return false;
    }

} // end of class BackTrackingSolver()
