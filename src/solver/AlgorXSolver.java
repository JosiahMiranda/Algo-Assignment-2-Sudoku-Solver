/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import java.util.HashSet;
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
	private int[] values;
	private int[][] intGrid;

	// Variables that store the exact cover matrix information
	private int[][] exactCoverMatrix;
	private int numCols;
	private int numRows;
	private int boxSize;

	// Variables that store the variables passed in for the alogithm x solve
	private Set<Integer> solution;

	// Using sets for these so that duplicates don't get added
	private Set<Integer> uncoveredRows;
	private Set<Integer> uncoveredCols;

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

		exactCoverMatrix = new int[numRows][numCols];

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
		// solution. Return its outcome, either true or false.
		return sudokuSolve(uncoveredRows, uncoveredCols, solution);
	} // end of solve()

	// at the beginning, all of the rows and columns should not be covered yet.
	private void fillUncoveredSets() {
		for (int row = 0; row < numRows; row++) {
			uncoveredRows.add(row);
		}

		for (int col = 0; col < numCols; col++) {
			uncoveredCols.add(col);
		}
	}

	// Covering all of the columns and rows that should be covered due to the
	// initial grid.
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
						if (exactCoverMatrix[realRowIndex][matrixCol] == 1) {
							for (int innerRow = 0; innerRow < numRows; innerRow++) {

								if (exactCoverMatrix[innerRow][matrixCol] == 1) {
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
			if (exactCoverMatrix[row][column] == 1) {
				// We keep track of the rows and columns we remove
				Set<Integer> rowsRemovedThisCall = new HashSet<Integer>();
				Set<Integer> colsRemovedThisCall = new HashSet<Integer>();

				// The solution is an array of integers, that stores the rows of the matrix
				// corresponding to the choices in the
				// sudoku. Thus we will keep track of a solution to add via this variable.
				// add the row to the partial solution
				Integer solutionToAdd = row;
				partialSolution.add(solutionToAdd);

				// for all columns in that row that have a 1 in them, remove it
				for (Integer col : uncoveredCols) {
					if (exactCoverMatrix[row][col] == 1) {
						colsRemovedThisCall.add(col);

						// also remove all of the rows in those columns that have a 1 in them
						for (Integer otherRow : uncoveredRows) {
							if (exactCoverMatrix[otherRow][col] == 1) {
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

	// Method that finds the column with the least number of ones. Used because of
	// the heuristic that
	// lets the algorithm find dead ends quicker once selecting columns with the
	// smallest number of ones.
	private int columnLeastOnes(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols) {
		int columnToReturn = -1;
		int min = numRows;

		for (Integer col : uncoveredCols) {
			int count = 0;
			for (Integer row : uncoveredRows) {
				count += exactCoverMatrix[row][col];
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
		int column = 0;
		for (int rowStart = 0; rowStart < numRows; rowStart += size) {
			addRowColumnConstraint(column, rowStart);
			++column;
		}

		// Filling in the portion of the matrix that has to do with the row constraints
		// Starts at the n^2 column index because it's the second portion of constraints
		int startOfColumns = sizesq;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addRowValueConstraint(startOfColumns, startOfRows);
			startOfColumns += size;
		}

		// Filling in the portion of the matrix that has to do with the column
		// constraints
		// starts at 2 * n^2 column index because it's the third portion of constraints
		startOfColumns = sizesq * 2;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addColumnValueConstraint(startOfColumns, startOfRows);
		}

		// Filling in the portion of the matrix that has to do with the box constraints
		// Starts at the 3 * n^2 column index because it's the fourth portion of
		// constraints.
		startOfColumns = sizesq * 3;
		int startOfRows = 0;
		int increment = sizesq * boxSize;

		for (int i = 0; i < numRows; i += increment) {
			for (int j = 0; j < boxSize; j++) {
				addBoxValueConstraint(startOfColumns, startOfRows);

				startOfRows += sizesq;
			}

			startOfColumns += (size * boxSize);
		}

	}

	// This method fills the first constraints, which are the row-column
	// constraints.
	// It goes through every n values and fills them up with 1s before incrementing
	// to the next column,
	// until all of the rows have been filled.

	// Method for adding in the individual 1 value.
	private void addRowColumnConstraint(int column, int rowStart) {
		for (int i = rowStart; i < rowStart + size; i++) {
			exactCoverMatrix[i][column] = 1;
		}
	}

	// Method fills in the second constraints, which is the row-value constraints of
	// the exact cover sudoku matrix.
	// It starts at every n^2 position as that's when it changes in column index.

	// Method adds in the individual 1s in a straight diagonal line pattern, only
	// doing n ones.
	private void addRowValueConstraint(int startOfColumns, int startOfRows) {
		int column = startOfColumns;
		int maxNumRow = startOfRows + sizesq;
		int maxNumCol = startOfColumns + size;

		for (int i = startOfRows; i < maxNumRow; ++i) {
			if (startOfColumns == maxNumCol) {
				startOfColumns = column;
			}

			exactCoverMatrix[i][startOfColumns] = 1;
			startOfColumns++;
		}
	}

	// Method adds the third constraints of the matrix, which are the column value
	// constraints.
	// It starts the pattern at every n^2 position, doing n^2 ones.

	// Method for adding the individual ones. This method is the one which does the
	// long diagonal line pattern.
	private void addColumnValueConstraint(int startOfColumns, int startOfRows) {
		int maxNumCol = startOfColumns + sizesq;
		for (int i = startOfColumns; i < maxNumCol; ++i) {
			exactCoverMatrix[startOfRows][i] = 1;
			++startOfRows;
		}
	}

	// final method for filling in the last constraints of the exact cover sudoku
	// matrix. Responsible for the box value
	// constraints. Does the same pattern square root of n times, with each
	// iteration shifting it to the next area in columns.

	// Method that adds the individual ones into each straight diagonal line.
	private void addBoxValueConstraint(int startOfColumns, int startOfRows) {
		int column = startOfColumns;
		int maxNumRow = startOfRows + sizesq;
		int maxNumCol = startOfColumns + size;
		int count = 0;
		for (int i = startOfRows; i < maxNumRow; ++i) {
			if (startOfColumns == maxNumCol) {
				startOfColumns = column;
				++count;
			}

			if (count >= boxSize) {
				startOfColumns += size;
				column = startOfColumns;
				maxNumCol = startOfColumns + size;
				count = 0;
			}

			exactCoverMatrix[i][startOfColumns] = 1;
			++startOfColumns;
		}
	}

} // end of class AlgorXSolver
