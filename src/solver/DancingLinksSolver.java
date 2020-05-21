/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver {
	// TODO: Add attributes as needed.

	// Variables that store grid information
	private int size;
	private int sizesq;
	private int[] values;
	private int[][] intGrid;

	// Variables that store the exact cover matrix information
	private int[][] baseExactCoverMatrix;
	private int[][] exactCoverMatrix;
	private int numCols;
	private int numRows;
	private int boxSize;

	private ColumnNode headerColumnNode;
	
	// Set of integers. Just like in Algorithm x, I'll use it to store the row indexes of the nodes
	private Set<Integer> solution;

	public DancingLinksSolver() {
		// TODO: any initialisation you want to implement.
		
		// intialising the solution
		solution = new HashSet<Integer>();
	} // end of DancingLinksSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		// TODO: your implementation of the dancing links solver for Killer Sudoku.
		
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

		// Creating the base exact cover matrix that has all of the 1s and 0s.
		baseExactCoverMatrix = new int[numRows][numCols];
		
		// Covering all the rows that should be covered from the initial grid and storing
		// it into this new cover matrix.
		exactCoverMatrix = createPartialExactCoverMatrix();

		// Turning that cover matrix into a grid of dancing links to be used by the algorithm.
		createDancingLinksGrid();

		// placeholder
		// Calling the sudoku solve method and returning its outcome
		return sudokuSolve();
	} // end of solve()

	// Dancing links sudoku solve algorithm
	private boolean sudokuSolve() {
		// Base case. If the headerColumnNode.right is the same as itself, then it circles back to the header, meaning
		// that there are no columns remaining, and thus the algorithm has found a proper solution.
		if (headerColumnNode.right == headerColumnNode) {
			
			// Same as algorithm x. Turns all of the rows in the solution Set and translates it into the sudoku grid.
			for (Integer solutionIndex : solution) {

				int row = getActualRowFromRealRow(solutionIndex);
				int col = getActualColFromRealRow(solutionIndex);
				int valueIndex = getValIndexFromRealRow(solutionIndex);

				intGrid[row][col] = values[valueIndex];
			}
			return true;
		}

		// We choose the column with the minimum number of ones for most efficient solving
		ColumnNode column = columnLeastOnes();

		// Then we need to cover this column as it will be used in the solution
		column.cover();

		// Iterate through every row. We start with the column's first node, then progressively go down
		// until it circles back to the column node.
		for (DancingLinksNode row = column.down; row != column; row = row.down) {
			
			// store the current row's index
			Integer realRowIndex = row.realRowIndex;

			// Add it to the solution.
			solution.add(realRowIndex);

			// Iterate through each column in that row, and cover it.
			for (DancingLinksNode adjacentCol = row.right; adjacentCol != row; adjacentCol = adjacentCol.right) {
				adjacentCol.column.cover();
			}

			// Then we call the recursive call. Return true if it is true.
			if (sudokuSolve()) {
				return true;
			}

			// If it reaches this stage then it hit a deadend. We need to backtrack. So, remove that added
			// row from the solution since it's incorrect.
			solution.remove(realRowIndex);

			// And then, iterate through each column in that row again. Except this time, go BACKWARDS, because it's crucial
			// that we uncover them in reverse order from how we covered them.
			for (DancingLinksNode adjacentCol = row.left; adjacentCol != row; adjacentCol = adjacentCol.left) {
				adjacentCol.column.uncover();
			}
		}
		
		// If this is reached then we hit a deadend with this column. Return false.
		column.uncover();
		
		return false;

	}

	// Method that finds the column with the least number of ones. Used because of
	// the heuristic that
	// lets the algorithm find dead ends quicker once selecting columns with the
	// smallest number of ones.
	private ColumnNode columnLeastOnes() {
		int leastOnes = numRows;
		ColumnNode column = null;
		for (ColumnNode tempColumn = (ColumnNode) headerColumnNode.right; tempColumn != headerColumnNode; tempColumn = (ColumnNode) tempColumn.right) {
			if (tempColumn.size < leastOnes) {
				column = tempColumn;
				leastOnes = column.size;
			}
		}
		return column;
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

	// This method fills the first constraints, which are the row-column constraints. 
	// It goes through every n values and fills them up with 1s before incrementing to the next column,
	// until all of the rows have been filled.
	private void fillRowColumnConstraintPart() {
		int column = 0;
		for (int rowStart = 0; rowStart < numRows; rowStart += size) {
			addRowColumnConstraint(column, rowStart);
			++column;
		}
	}

	// Method for adding in the individual 1 value.
	private void addRowColumnConstraint(int column, int rowStart) {
		for (int i = rowStart; i < rowStart + size; i++) {
			baseExactCoverMatrix[i][column] = 1;
		}
	}

	// Method fills in the second constraints, which is the row-value constraints of the exact cover sudoku matrix.
	// It starts at every n^2 position as that's when it changes in column index.
	private void fillRowValueConstraintPart() {
		
		// Starts at the n^2 column index because it's the second portion of constraints
		int startOfColumns = sizesq;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addRowValueConstraint(startOfColumns, startOfRows);
			startOfColumns += size;
		}
	}

	// Method adds in the individual 1s in a straight diagonal line pattern, only doing n ones.
	private void addRowValueConstraint(int startOfColumns, int startOfRows) {
		int column = startOfColumns;
		int maxNumRow = startOfRows + sizesq;
		int maxNumCol = startOfColumns + size;

		for (int i = startOfRows; i < maxNumRow; ++i) {
			if (startOfColumns == maxNumCol) {
				startOfColumns = column;
			}

			baseExactCoverMatrix[i][startOfColumns] = 1;
			startOfColumns++;
		}
	}

	// Method adds the third constraints of the matrix, which are the column value constraints.
	// It starts the pattern at every n^2 position, doing n^2 ones.
	private void fillColumnValueConstraintPart() {
		
		// starts at 2 * n^2 column index because it's the third portion of constraints
		int startOfColumns = sizesq * 2;

		for (int startOfRows = 0; startOfRows < numRows; startOfRows += sizesq) {
			addColumnValueConstraint(startOfColumns, startOfRows);
		}
	}

	// Method for adding the individual ones. This method is the one which does the long diagonal line pattern.
	private void addColumnValueConstraint(int startOfColumns, int startOfRows) {
		int maxNumCol = startOfColumns + sizesq;
		for (int i = startOfColumns; i < maxNumCol; ++i) {
			baseExactCoverMatrix[startOfRows][i] = 1;
			++startOfRows;
		}
	}

	// final method for filling in the last constraints of the exact cover sudoku matrix. Responsible for the box value
	// constraints. Does the same pattern square root of n times, with each iteration shifting it to the next area in columns.
	private void fillBoxValueConstraintPart() {

		// Starts at the 3 * n^2 column index because it's the fourth portion of constraints.
		int startOfColumns = sizesq * 3;
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

			baseExactCoverMatrix[i][startOfColumns] = 1;
			++startOfColumns;
		}
	}

	// This method is the one which creates the matrix that takes into consideration all of the values that are
	// already in the initial grid. 
	private int[][] createPartialExactCoverMatrix() {
		createMatrix();
		int[][] newCoverMatrix = baseExactCoverMatrix;

		// Iterate through every row and column in the sudoku grid
		for (int row = 0; row < size; row++) {

			for (int column = 0; column < size; column++) {

				int value = intGrid[row][column];
				// If the value is not empty, then we proceed
				if (value != -1) {
					
					// For each of the values in the possible array of values,
					for (int val : values) {
						// If the current value is not equal to what the grid actually has, then fill all corresponding
						// rows in the exact cover matrix.
						if (val != value) {
							Arrays.fill(newCoverMatrix[getRealRowIndex(row, column, getValueIndex(val))], 0);
						}
					}
				}
			}
		}

		// finally return the new cover matrix
		return newCoverMatrix;
	}

	// Method for converting the initial cover matrix into a dancing links grid.
	private void createDancingLinksGrid() {

		// Instantiate the header column node.
		headerColumnNode = new ColumnNode("headerColumnNode");

		// Create array of all columns.
		ColumnNode[] columns = new ColumnNode[numCols];

		// Instantiate all of the columns and iterate through them with the header node so that we can ensure
		// the circular link towards them
		for (int col = 0; col < numCols; ++col) {
			String name = Integer.toString(col);
			ColumnNode column = new ColumnNode(name);
			columns[col] = column;
			headerColumnNode = (ColumnNode) headerColumnNode.addNodeToTheRight(column);

		}

		// Need to reset the header back to itself.
		headerColumnNode = headerColumnNode.right.column;

		// keeping track of the matrix row index so we can use that for the partial solution
		int realRowIndex = 0;
		for (int[] row : exactCoverMatrix) {

			DancingLinksNode current = null;

			for (int col = 0; col < numCols; ++col) {

				// Whenever there is a 1, create a dancing links node at that column and essentially, row.
				if (row[col] == 1) {

					ColumnNode column = columns[col];

					DancingLinksNode node = new DancingLinksNode(column, realRowIndex);

					// If the current node is empty then it's the first one.
					if (current == null)
						current = node;

					// We add the node onto the bottom of the column since we're going down the rows. This is a handy way
					// to use the circular referencing and keeping the setting up of the dancing links grid quick.
					column.up.addNodeBelow(node);

					// We must also set it to the right side of any current nodes in that row. Still fine for circular referencing.
					current = current.addNodeToTheRight(node);

					++column.size;
				}
			}
			
			++realRowIndex;
		}

		headerColumnNode.size = numCols;
	}

	// Method that takes in a row, column, and value index of a sudoku grid
	// possibility and then translates it
	// into a row index inside of the exact cover matrix.
	private int getRealRowIndex(int row, int col, int valueIndex) {
		int realRowIndex = sizesq * row + size * col + valueIndex;
		return realRowIndex;
	}

	private int getValueIndex(int value) {
		for (int i = 0; i < size; i++) {
			if (values[i] == value) {
				return i;
			}
		}
		return 0;
	}

} // end of class DancingLinksSolver
