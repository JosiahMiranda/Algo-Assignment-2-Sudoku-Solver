/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;


/**
 * Backtracking solver for Killer Sudoku.
 */
public class KillerBackTrackingSolver extends KillerSudokuSolver
{
    // TODO: Add attributes as needed.

    public KillerBackTrackingSolver() {
        // TODO: any initialisation you want to implement.
    } // end of KillerBackTrackingSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        // TODO: your implementation of the backtracking solver for standard Sudoku.
    	
    	KillerSudokuGrid killerGrid = (KillerSudokuGrid) grid;
    	int size = killerGrid.size;
    	int[][] intGrid = killerGrid.grid;
    	int[] values = killerGrid.values;
    	Cage[] cages = killerGrid.cages;
    	
    	if (sudokuSolve(intGrid, values, size, cages)) {
    		return true;
    	}

        return false;
    } // end of solve()
    
    public boolean sudokuSolve(int[][] grid, int[] values, int size, Cage[] cages) {
    	
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
    		if (validMove(grid, row, col, value, cages)) {
    			grid[row][col] = value;
    			if (sudokuSolve(grid, values, size, cages)) {
    				return true;
    			} else {
    				grid[row][col] = -1;
    			}
    		}
    	}
    	return false;
    }

} // end of class KillerBackTrackingSolver()
