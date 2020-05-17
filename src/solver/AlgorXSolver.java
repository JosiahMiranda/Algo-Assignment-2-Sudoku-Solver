package solver;
/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	// TODO: Add attributes as needed.

	int size;
	int[] values;
	int numCols;
	int numRows;

	List<Integer> solution = new ArrayList<Integer>();
	int[][] matrix;
	List<Integer> coveredRows = new ArrayList<Integer>();
	List<Integer> coveredCols = new ArrayList<Integer>();

	int[][] intGrid;

	public AlgorXSolver() {
		// TODO: any initialisation you want to implement.

	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		// TODO: your implementation of the Algorithm X solver for standard Sudoku.

		StdSudokuGrid stdGrid = (StdSudokuGrid) grid;
		size = stdGrid.size;
		values = stdGrid.values;
		intGrid = stdGrid.grid;

		numCols = (size * size) * 4;
		numRows = (size * size * size);

		matrix = new int[numRows][numCols];

		fillInitialConstraints();

		Set<Integer> uncoveredRows = new HashSet<Integer>();
		Set<Integer> uncoveredCols = new HashSet<Integer>();

		fillUncoveredLists(uncoveredRows, uncoveredCols);

		setInitialPartialSolution(uncoveredRows, uncoveredCols);
	
		if (sudokuSolve(uncoveredRows, uncoveredCols, solution)) {
			return true;
		}

		return false;
	} // end of solve()

	private boolean sudokuSolve(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols, List<Integer> partialSolution) {

		Integer solutionToAdd = null;

		// If the matrix is empty, return true. This is a valid solution
		if (uncoveredCols.size() == 0) {

			// Placing the solution into the grid
			for (Integer sol : partialSolution) {
				int row = getActualRowFromRealRow(sol);
				int col = getActualColFromRealRow(sol);
				// Have to minus one since array indexes start at 0
				int valIndex = getValIndexFromRealRow(sol)-1;

				intGrid[row][col] = values[valIndex];
			}
			return true;
		}

		// Choose the column with the least amount of ones left in the matrix
		Integer column = columnWithLeastOnes(uncoveredRows, uncoveredCols);


		// if the column only has zeros left then return false
		if (onlyZerosLeft(column, uncoveredRows)) {
			return false;
		} else {

			// Then choose a row for that column where there is a 1
			for (Integer row : uncoveredRows) {
				if (matrix[row][column] == 1) {


					Set<Integer> rowsRemovedThisCall = new HashSet<Integer>();
					Set<Integer> colsRemovedThisCall = new HashSet<Integer>();

					// Add this row to the partial solution
					solutionToAdd = row;
					partialSolution.add(solutionToAdd);

					// Now loop through the columns to find all the columns in that row with a 1
					for (Integer col : uncoveredCols) {
						if (matrix[row][col] == 1) {

							colsRemovedThisCall.add(col);

							for (Integer otherRow : uncoveredRows) {
								if (matrix[otherRow][col] == 1) {
									rowsRemovedThisCall.add(otherRow);
								}
							}

						}
					}

					// Edited lists to be used in the next call
					Set<Integer> partUncoveredRows = new HashSet<Integer>(uncoveredRows);
					Set<Integer> partUncoveredCols = new HashSet<Integer>(uncoveredCols);

					// Covering the rows for the next list to be used in recursive step
					for (Integer r : rowsRemovedThisCall) {
						partUncoveredRows.remove(r);
					}

					// Covering the cols for the next list to be used in recursive step
					for (Integer c : colsRemovedThisCall) {
						partUncoveredCols.remove(c);
					}


					// Recursive step
					if (sudokuSolve(partUncoveredRows, partUncoveredCols, partialSolution)) {
						return true;
					}

					// if this gets reached then we reached a deadend. Need to do undo changes.

					partialSolution.remove(solutionToAdd);

				}
			}

			return false;
		}

	}

	private void fillUncoveredLists(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols) {
		for (int row = 0; row < numRows; ++row) {
			uncoveredRows.add(row);
		}

		for (int col = 0; col < numCols; ++col) {
			uncoveredCols.add(col);
		}
	}

	private boolean onlyZerosLeft(int column, Set<Integer> uncoveredRows) {

		for (Integer row : uncoveredRows) {
			if (matrix[row][column] == 1) {
				return false;
			}
		}

		return true;
	}
	
	private int columnWithLeastOnes(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols) {
		int column = -1;
		int min = numRows;
		
		// Iterate through every column
		for (Integer col : uncoveredCols) {
			int count = 0;
			for (Integer row : uncoveredRows) {
				count += matrix[row][col];
			}
			
			// If the number of 1s is less than the min, then set the minimum to the count and assign the column
			if (count < min) {
				min = count;
				column = col;
			}
		}
		
		return column;
	}

	// for testing
	private void testWithUncovered(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols, String fileName) {
		System.out.println("testing with uncovered rows and columns");

		String outputString = "Columns:";

		for (Integer c : uncoveredCols) {
			outputString += c + "\t";
		}
		outputString += "\n";

		for (Integer r : uncoveredRows) {
			outputString += r + ":\t";
			for (Integer c : uncoveredCols) {
				outputString += matrix[r][c] + "\t";
			}
			outputString += "\n";
		}

		try {
			PrintWriter outWriter = new PrintWriter(new FileWriter(fileName), true);
			outWriter.print(outputString);
			outWriter.close();

		} catch (IOException ex) {
			System.err.println(String.format("File: %s not found."));
		}
	}

	// for testing
	private void testFile() {
		System.out.println("test file");
		String outputString = "";
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {

//				System.out.println("On row " + row + ", col " + col);
				if (coveredRows.contains(row) || coveredCols.contains(col)) {
					outputString += ". ";
				} else {
					outputString += matrix[row][col] + " ";
				}
			}
			outputString += "\n";
		}

		try {
			PrintWriter outWriter = new PrintWriter(new FileWriter("test.txt"), true);
			outWriter.print(outputString);
			outWriter.close();

		} catch (IOException ex) {
			System.err.println(String.format("File: %s not found."));
		}

	}

	private void setInitialPartialSolution(Set<Integer> uncoveredRows, Set<Integer> uncoveredCols) {
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				// Covering all the initial values inside of the grid already
				if (intGrid[row][col] != -1) {
					// When we find a value, we get its value index for the formulas and then
					// proceed to cover them
					int valueIndex = getValIndex(intGrid[row][col]);
					// Have to plus one because grid row and col start at 0 whereas formula starts
					// at 1
					int realRowIndex = realRowIndex(row + 1, col + 1, valueIndex, size);
					solution.add(realRowIndex);

					// Iterate through every column
					for (int c = 0; c < numCols; ++c) {

						// Find the value of the row, in this case the real row index. If its one loop
						// through
						if (!coveredCols.contains(c) && matrix[realRowIndex][c] == 1) {

							if (!coveredRows.contains(realRowIndex)) {

								// and if a row's value is one, then cover it
								for (int rowIn = 0; rowIn < numRows; ++rowIn) {

									if (!coveredRows.contains(rowIn) && matrix[rowIn][c] == 1) {
										coveredRows.add(rowIn);
										uncoveredRows.remove(rowIn);
									}
								}
							}
							coveredCols.add(c);
							uncoveredCols.remove(c);
						}
					}
				}
			}
		}

	}

	private void fillInitialConstraints() {
		for (int row = 1; row <= size; ++row) {
			for (int col = 1; col <= size; ++col) {
				for (int value = 1; value <= size; ++value) {
					int matrixRow = realRowIndex(row, col, value, size);
					matrix[matrixRow][getCellConstraintIndex(row, col, value, size)] = 1;
					matrix[matrixRow][getRowConstraintIndex(row, col, value, size)] = 1;
					matrix[matrixRow][getColumnConstraintIndex(row, col, value, size)] = 1;
					matrix[matrixRow][getBlockConstraintIndex(row, col, value, size)] = 1;
				}
			}

		}
	}

	private int getValIndexFromRealRow(int realRowIndex) {
		int valIndex = (realRowIndex % size) + 1;
		return valIndex;
	}

	private int getActualRowFromRealRow(int realRowIndex) {
		int actualRow = (int) Math.floor((realRowIndex) / (size * size));
		return actualRow;
	}

	private int getActualColFromRealRow(int realRowIndex) {
		int innerFloor = ((int) Math.floor((realRowIndex / (size * size))) * (size * size));
		int actualCol = (int) Math.floor((realRowIndex - innerFloor) / size);
		return actualCol;
	}

	// have to return index + 1 for the way my formula is
	private int getValIndex(int value) {
		for (int i = 0; i < size; ++i) {
			if (values[i] == value) {
				return i + 1;
			}
		}

		return 0;
	}

	private int getCellConstraintIndex(int row, int col, int valueIndex, int size) {
		int matrixColumnIndex = size * row - (size - col) - 1;
		return matrixColumnIndex;
	}

	private int getRowConstraintIndex(int row, int col, int valueIndex, int size) {
		int matrixColumnIndex = (size * size) + (size * (row - 1)) + valueIndex - 1;
		return matrixColumnIndex;
	}

	private int getColumnConstraintIndex(int row, int col, int valueIndex, int size) {
		int matrixColumnIndex = 2 * (size * size) + (size * (col - 1)) + valueIndex - 1;
		return matrixColumnIndex;
	}

	private int getBlockConstraintIndex(int row, int col, int valueIndex, int size) {
		double sqrt = Math.sqrt(size);
		int block = (int) ((sqrt * Math.floor((row - 1) / sqrt)) + Math.ceil(col / sqrt));
		int matrixColumnIndex = 3 * (size * size) + (block - 1) * size + valueIndex - 1;
		return matrixColumnIndex;
	}

	private int realRowIndex(int row, int col, int valueIndex, int size) {
		int index = size * size * (row - 1) + size * (col - 1) + valueIndex - 1;
		return index;
	}

} // end of class AlgorXSolver
