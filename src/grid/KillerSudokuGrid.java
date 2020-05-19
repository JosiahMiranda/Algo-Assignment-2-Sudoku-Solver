/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import solver.Cage;
import solver.Tuple;

/**
 * Class implementing the grid for Killer Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task E and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class KillerSudokuGrid extends SudokuGrid {
	// TODO: Add your own attributes

	public int[][] grid;
	public int size;
	public int[] values;
	public Cage[] cages;

	public KillerSudokuGrid() {
		super();

		// TODO: any necessary initialisation at the constructor
	} // end of KillerSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		// TODO
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line;

		// Read in size
		line = reader.readLine();
		size = Integer.parseInt(line);

		// prepare values and grid size
		values = new int[size];
		grid = new int[size][size];

		// Read in line of values
		line = reader.readLine();
		String[] strValues = line.split(" ");

		// Add each of these values to the values array
		for (int i = 0; i < size; ++i) {
			int valueToAdd = Integer.parseInt(strValues[i]);
			values[i] = valueToAdd;
		}

		// Initially set all grid values to -1 to denote an empty space
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				grid[row][col] = -1;
			}
		}

		// Read in number of cages and prepare array of cages
		line = reader.readLine();
		int numCages = Integer.parseInt(line);
		cages = new Cage[numCages];

		// Read in all cage information and create cages and tuples within cages
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(" ");
			int sum = Integer.parseInt(tokens[0]);
			Tuple[] tuples = new Tuple[tokens.length - 1];
			for (int i = 1; i < tokens.length; ++i) {
				String[] strTuple = tokens[i].split(",");
				int row = Integer.parseInt(strTuple[0]);
				int col = Integer.parseInt(strTuple[1]);
				Tuple tuple = new Tuple(row, col);

				// i-1 because the tuples in the lines start at the 1st index not the 0th
				tuples[i - 1] = tuple;
			}
			Cage cage = new Cage(sum, tuples);
			cages[counter] = cage;
			++counter;
		}

		reader.close();

	} // end of initBoard()

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		// TODO

		// Just print the tostring
		try {
			PrintWriter outWriter = new PrintWriter(new FileWriter(filename), true);
			outWriter.print(toString());
			outWriter.close();

		} catch (FileNotFoundException ex) {
			System.err.println(String.format("File: %s not found.", filename));
		}

	} // end of outputBoard()

	// Print out the grid. If it's empty, replace it with a '.' rather than a '-1'
	@Override
	public String toString() {
		// TODO

		String output = "";
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (grid[row][col] != -1) {
					output += grid[row][col];
				} else {
					output += ".";
				}
				if (col != size - 1) {
					output += ",";
				}
			}
			output += "\n";
		}

		return output;
	} // end of toString()

	// Ensure that the entire grid is valid.
	@Override
	public boolean validate() {

		// check that every row and column is valid
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (!validCell(row, col)) {
					return false;
				}
			}
		}
		
		// Then check that every cage is valid
		for (Cage cage : cages) {
			int sum = 0;
			Set<Integer> uniqueValues = new HashSet<Integer>();
			for (Tuple tuple : cage.tuples) {
				// if it fails to add a value then it's not unique and so invalid
				if (!uniqueValues.add(grid[tuple.row][tuple.col])) {
					return false;
				}
				sum += grid[tuple.row][tuple.col];
			}
			// If the sum at the end does not equal the cages sum then it is invalid
			if (sum != cage.sum) {
				return false;
			}
		}

		return true;
	} // end of validate()

	public boolean validCell(int row, int col) {

		int value = grid[row][col];

		int numValues = 0;
		for (int i = 0; i < size; i++) {
			if (grid[row][i] == value) {
				++numValues;
			}
			if (numValues > 1) {
				return false;
			}
		}

		numValues = 0;
		for (int i = 0; i < size; i++) {
			if (grid[i][col] == value) {
				++numValues;
			}
			if (numValues > 1) {
				return false;
			}
		}

		int squareRoot = (int) Math.sqrt(size);
		int blockRowStartIndex = row - row % squareRoot;
		int blockColStartIndex = col - col % squareRoot;

		ArrayList<Integer> valuesPresent = new ArrayList<Integer>();

		for (int r = blockRowStartIndex; r < blockRowStartIndex + squareRoot; ++r) {
			for (int c = blockColStartIndex; c < blockColStartIndex + squareRoot; ++c) {
				if (valuesPresent.contains(grid[r][c])) {
					return false;
				} else {
					valuesPresent.add(grid[r][c]);
				}
			}
		}

		return true;
	}

} // end of class KillerSudokuGrid
