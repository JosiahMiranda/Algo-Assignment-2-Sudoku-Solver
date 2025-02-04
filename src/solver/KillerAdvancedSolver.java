/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.Arrays;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;


/**
 * Your advanced solver for Killer Sudoku.
 */
public class KillerAdvancedSolver extends KillerSudokuSolver
{
    // TODO: Add attributes as needed.

    public KillerAdvancedSolver() {
        // TODO: any initialisation you want to implement.
    } // end of KillerAdvancedSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        // TODO: your implementation of the backtracking solver for standard Sudoku.
    	
    	KillerSudokuGrid killerGrid = (KillerSudokuGrid) grid;
    	int size = killerGrid.size;
    	int[][] intGrid = killerGrid.grid;
    	int[] values = killerGrid.values;
    	// we sort the array so that the smaller values are tried first.
    	Arrays.sort(values);
    	Cage[] cages = killerGrid.cages;
    		
    	if (sudokuSolve(intGrid, values, size, cages, 0)) {
    		return true;
    	}

        return false;
    } // end of solve()
    
    public boolean sudokuSolve(int[][] grid, int[] values, int size, Cage[] cages, int level) {
    	
    	int row = -1;
    	int col = -1;
    	boolean noEmptyRemaining = true;
    	
    	// we search through each cage's cells rather than going through the board,
    	// left to right, top to bottom.
    	Cage cageForSolve = null;
    	
    	for (Cage cage : cages) {
    		for (Tuple tuple : cage.tuples) {
    			if (grid[tuple.row][tuple.col] == -1) {
    				noEmptyRemaining = false;
    				cageForSolve = cage;
    				row = tuple.row;
    				col = tuple.col;
    				break;
    			}
    		}
    		// early termination
    		if (!noEmptyRemaining) {
    			break;
    		}
    	}
    	
    	// if no empty cells in any cages, then a solution has been found
    	if (noEmptyRemaining) {
    		return true;
    	}
    	
    	// Try each each value in the values array
    	for (int value : values) {
    		if (validMove(grid, row, col, value, cageForSolve)) {
    			// Try setting the current row and col to that value
    			grid[row][col] = value;
    			// Run recursive step
    			if (sudokuSolve(grid, values, size, cages, level + 1)) {
    				return true;
    			} else {
    				// If this is run, then the earlier set value was an invalid move. Set back to -1.
    				grid[row][col] = -1;
    			}
    		}
    	}
    	return false;
    }
    
 // check that a move is valid
 	public boolean validMove(int[][] grid, int row, int col, int value, Cage cage) {

 		int size = grid.length;
 		
 		// Check that the value doesn't already exist in the row
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

 		// Now do the cage check. Find the cage that the row and column is in.
// 		Cage cage = findCage(row, col, cages);
 		
 		if (cage == null) {
 			return false;
 		} else {
 			
 			// Find tuples and the number of empty values in the cage.
 			Tuple[] tuples = cage.tuples;
 			int numEmpties = 0;
 			int total = 0;
 			
 			for (Tuple tuple : tuples) {
 				// If the tuple value is -1 still, then it's empty. Increment number of empty
 				// values in the cage.
 				if (grid[tuple.row][tuple.col] == -1) {
 					++numEmpties;
 				} else if (grid[tuple.row][tuple.col] == value) {
 					// if the value is already inside of the cage then return false
 					return false;
 				} else {
 					// If it's not empty then we simply add the value of that tuple's value.
 					total += grid[tuple.row][tuple.col];
 				}
 			}
 			
 			
 			if (numEmpties == 1) {
 				// If the num of empties is 1, then this last value that we add
 				// needs to equal to the cage's sum.
 				if ((total + value) != cage.sum) {
 					// If it doesn't return false.
 					return false;
 				}
 				
 			} else {
 				// If the num of empties doesn't equal to one, meaning there are more empty values,
 				// then if the total + value is > than the sum, it's wrong. Return false.
 				if ((total + value) > cage.sum) {
 					return false;
 				}
 			}
 		}
 		
 		// If the program didn't return false, then it returns true as this move is valid
 		return true;

 	}

} // end of class KillerAdvancedSolver
