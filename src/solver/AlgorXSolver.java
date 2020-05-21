/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Algorithm X solver for standard Sudoku.
 */

public class AlgorXSolver extends StdSudokuSolver {

	// Variables that store grid information
	private int size;
	private int sizesq;
	int[] values;
	int[][] intGrid;

	// Variables that store the exact cover matrix information
	int[][] matrix;
	int numCols;
	int numRows;
	int boxSize;

	// Variables that store the variables passed in for the alogithm x solve
	Set<Integer> solution;

	// Using sets for these so that duplicates don't get added
	Set<Integer> uncoveredRows;
	Set<Integer> uncoveredCols;

	public AlgorXSolver() {

		// Instantiate the collections that will be used throughout the solve
		solution = new HashSet<Integer>();
		uncoveredRows = new HashSet<Integer>();
		uncoveredCols = new HashSet<Integer>();

	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {

		// Defining all of the variables that will be used
		StdSudokuGrid stdGrid = (StdSudokuGrid) grid;
		size = stdGrid.size;
		sizesq = (size * size);
		values = stdGrid.values;
		intGrid = stdGrid.grid;

		// Setting up matrix sizes. An exact cover matrix for sudoku puzzle of size n
		// will always have
		// a number of rows that is size^3 and a number of columns that is (n^2) * 4.
		numRows = size * size * size;
		numCols = sizesq * 4;
		boxSize = (int) Math.sqrt(size);

		matrix = new int[numRows][numCols];

		// Method for creating the matrix using patterns.
		createMatrix();

		// Make the uncoveredRows and uncoveredCols sets filled with all of the rows and
		// columns at the start,
		// since at the start we want to consider all of them.
		fillUncoveredSets();

		// Then take in the values of the grid and create the partial solution based off
		// of the choices already made
		// in the sudoku puzzle
		setInitialPartialSolution();

		// The solve method initial call. Pass in the first instance of the sets and
		// solution.
		if (sudokuSolve(uncoveredRows, uncoveredCols, solution)) {
			return true;
		}

		// if this is reached, no solution could be found
		return false;
	} // end of solve()

	private void fillUncoveredSets() {
		for (int row = 0; row < numRows; row++) {
			uncoveredRows.add(row);
		}

		for (int col = 0; col < numCols; col++) {
			uncoveredCols.add(col);
		}
	}

	private void setInitialPartialSolution() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (intGrid[row][col] != -1) {

					// for all the values that are not empty in the grid, add them into the partial
					// solution
					int valueIndex = getValueIndex(intGrid[row][col]);
					int realRowIndex = getRealRowIndex(row, col, valueIndex);
					solution.add(realRowIndex);

					// Also remove them from the uncoveredRows and uncoveredCols sets because they
					// should be
					// 'deleted' from the matrix that is being used for the recursive solve.
					for (int matrixCol = 0; matrixCol < numCols; matrixCol++) {
						if (matrix[realRowIndex][matrixCol] == 1) {
							for (int innerRow = 0; innerRow < numRows; innerRow++) {

								if (matrix[innerRow][matrixCol] == 1) {
									uncoveredRows.remove(innerRow);
								}
							}
							uncoveredCols.remove(matrixCol);
						}
					}
				}
			}
		}
	}

	// THE SOLVE METHOD THAT TOOK ME LITERAL DAYS TO GET WORKING IF THIS SCREWS UP
	// IM GONNA FLIP
	private boolean sudokuSolve(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols, Set<Integer> partialSolution) {

		// The solution is an array of integers, that stores the rows of the matrix
		// corresponding to the choices in the
		// sudoku. Thus we will keep track of a solution to add via this variable.
		Integer solutionToAdd = null;

		// Base case. If the uncovered cols size is 0 then the matrix being worked with
		// is empty so return true.
		if (uncoveredCols.size() == 0) {

			// Translate partial solution matrix rows into actual rows, columns, and values
			// in the grid.
			for (Integer solutionIndex : partialSolution) {

				int row = getActualRowFromRealRow(solutionIndex);
				int col = getActualColFromRealRow(solutionIndex);
				int valueIndex = getValIndexFromRealRow(solutionIndex);

				intGrid[row][col] = values[valueIndex];
			}
			return true;
		}

		Integer column = columnLeastOnes(uncoveredRows, uncoveredCols);
		
		// Choose a row in the matrix
		for (Integer row : uncoveredRows) {
			if (matrix[row][column] == 1) {
				// We keep track of the rows and columns we remove
				Set<Integer> rowsRemovedThisCall = new HashSet<Integer>();
				Set<Integer> colsRemovedThisCall = new HashSet<Integer>();

				// And add the row to the partial solution
				solutionToAdd = row;
				partialSolution.add(solutionToAdd);

				// for all columns in that row that have a 1 in them, remove it
				for (Integer col : uncoveredCols) {
					if (matrix[row][col] == 1) {
						colsRemovedThisCall.add(col);

						// also remove all of the rows in those columns that have a 1 in them
						for (Integer otherRow : uncoveredRows) {
							if (matrix[otherRow][col] == 1) {
								rowsRemovedThisCall.add(otherRow);
							}
						}
					}
				}

				// Create new sets that will be passed in, that are the result of this recursive
				// call
				Set<Integer> partUncoveredRows = new HashSet<Integer>(uncoveredRows);
				Set<Integer> partUncoveredCols = new HashSet<Integer>(uncoveredCols);

				// Remove the rows
				for (Integer rowRemoved : rowsRemovedThisCall) {
					partUncoveredRows.remove(rowRemoved);
				}

				// Remove the columns
				for (Integer colRemoved : colsRemovedThisCall) {
					partUncoveredCols.remove(colRemoved);
				}

				// Do the recursive step with the reduced rows and columns of the matrix. Also
				// pass in the solution
				if (sudokuSolve(partUncoveredRows, partUncoveredCols, partialSolution)) {
					return true;
				}

				// If this is reached, then this recursive call was incorrect. Remove the added
				// Integer from the partial solution.
				partialSolution.remove(solutionToAdd);
			}
		}
		return false;
	}

	// Method that finds the column with the least number of ones. Used because the
	// algorithm states it is more efficient
	// this way.
	private int columnLeastOnes(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols) {
		int columnToReturn = -1;
		int min = numRows;

		for (Integer col : uncoveredCols) {
			int count = 0;
			for (Integer row : uncoveredRows) {
				count += matrix[row][col];
			}

			if (count < min) {
				min = count;
				columnToReturn = col;
			}
		}

		return columnToReturn;
	}

	// Method that takes in a row, column, and value index of a sudoku grid
	// possibility and then translates it
	// into a row index inside of the exact cover matrix.
	private int getRealRowIndex(int row, int col, int valueIndex) {
		int realRowIndex = sizesq * row + size * col + valueIndex;
		return realRowIndex;
	}

	// Method that takes in a row from the exact cover matrix and translates it into
	// the value index of
	// that corresponding row
	private int getValIndexFromRealRow(int realRowIndex) {
		int valIndex = realRowIndex % size;
		return valIndex;
	}

	// Method that takes in a row from the exact cover matrix and translates it into
	// the actual row in the sudoku grid.
	private int getActualRowFromRealRow(int realRowIndex) {
		int actualRow = (int) Math.floor(realRowIndex / sizesq);
		return actualRow;
	}

	// Method that takes in a row from the exact cover matrix and translates it into
	// the actual column in the sudoku grid.
	private int getActualColFromRealRow(int realRowIndex) {
		int innerfloor = ((int) Math.floor(realRowIndex / sizesq) * sizesq);
		int actualCol = (int) Math.floor((realRowIndex - innerfloor) / size);
		return actualCol;
	}

	// Returns the index of the value in the value array. Useful for using our
	// formulas to deduct the row, col, and value, of
	// a row in the matrix.
	private int getValueIndex(int value) {
		for (int i = 0; i < size; i++) {
			if (values[i] == value) {
				return i;
			}
		}
		return 0;
	}

	// METHODS FOR CREATION OF EXACT COVER MATRIX

	private void createMatrix() {

		// Filling in the portion of the matrix that has to do with the cell constraints
		fillRowColumnConstraintPart();

		// Filling in the portion of the matrix that has to do with the row constraints
		fillRowValueConstraintPart();

		// Filling in the portion of the matrix that has to do with the column
		// constraints
		fillColumnValueConstraintPart();

		// Filling in the portion of the matrix that has to do with the box constraints
		fillBoxValueConstraintPart();

	}

	public void fillRowColumnConstraintPart() {
		int column = 0;
		for (int rowStart = 0; rowStart < numRows; rowStart += size) {
			addCellConstraint(column, rowStart);
			column++;
		}
	}

	public void addCellConstraint(int column, int rowStart) {
		for (int i = rowStart; i < rowStart + size; i++) {
			matrix[i][column] = 1;
		}
	}

	public void fillRowValueConstraintPart() {
		int startOfColumns = sizesq;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addRowConstraint(startOfColumns, startOfRows);
			startOfColumns += size;
		}
	}

	public void addRowConstraint(int startOfColumns, int startOfRows) {
		int column = startOfColumns;
		int maxNumRow = startOfRows + sizesq;
		int maxNumCol = startOfColumns + size;

		for (int i = startOfRows; i < maxNumRow; ++i) {
			if (startOfColumns == maxNumCol) {
				startOfColumns = column;
			}

			matrix[i][startOfColumns] = 1;
			startOfColumns++;
		}
	}

	public void fillColumnValueConstraintPart() {
		int startOfColumns = sizesq * 2;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addColumnConstraint(startOfColumns, startOfRows);
		}
	}

	public void addColumnConstraint(int startOfColumns, int startOfRows) {
		int maxNumCol = startOfColumns + sizesq;
		for (int i = startOfColumns; i < maxNumCol; ++i) {
			matrix[startOfRows][i] = 1;
			startOfRows++;
		}
	}

	public void fillBoxValueConstraintPart() {

		int startOfColumns = sizesq * 3;
		int startOfRows = 0;
		int increment = sizesq * boxSize;

		for (int i = 0; i < numRows; i += increment) {
			for (int j = 0; j < boxSize; j++) {
				addBoxConstraint(startOfColumns, startOfRows);

				startOfRows += sizesq;
			}

			startOfColumns += (size * boxSize);
		}
	}

	public void addBoxConstraint(int startOfColumns, int startOfRows) {
		int column = startOfColumns;
		int maxNumRow = startOfRows + sizesq;
		int maxNumCol = startOfColumns + size;
		int count = 0;
		for (int i = startOfRows; i < maxNumRow; ++i) {
			if (startOfColumns == maxNumCol) {
				startOfColumns = column;
				count++;
			}

			if (count >= boxSize) {
				startOfColumns += size;
				column = startOfColumns;
				maxNumCol = startOfColumns + size;
				count = 0;
			}

			matrix[i][startOfColumns] = 1;
			startOfColumns++;
		}
	}

} // end of class AlgorXSolver
