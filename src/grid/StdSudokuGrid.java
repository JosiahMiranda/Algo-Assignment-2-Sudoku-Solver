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

/**
 * Class implementing the grid for standard Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task A and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {
	// TODO: Add your own attributes

	public int[][] grid;
	public int size;
	public int[] values;

	public StdSudokuGrid() {
		super();

		// TODO: any necessary initialisation at the constructor
	} // end of StdSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line;

		line = reader.readLine();
		size = Integer.parseInt(line);

		values = new int[size];
		grid = new int[size][size];

		line = reader.readLine();
		String[] strValues = line.split(" ");

		for (int i = 0; i < size; ++i) {
			int valueToAdd = Integer.parseInt(strValues[i]);
			values[i] = valueToAdd;
		}

		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				grid[row][col] = -1;
			}
		}

		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(" ");
			String[] tuple = tokens[0].split(",");

			int row = Integer.parseInt(tuple[0]);
			int col = Integer.parseInt(tuple[1]);

			int value = Integer.parseInt(tokens[1]);

			grid[row][col] = value;
		}

		reader.close();

	} // end of initBoard()

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		// TODO

		try {
			PrintWriter outWriter = new PrintWriter(new FileWriter(filename), true);
			outWriter.print(toString());
			outWriter.close();

		} catch (FileNotFoundException ex) {
			System.err.println(String.format("File: %s not found.", filename));
		}

	} // end of outputBoard()

	@Override
	public String toString() {
		// TODO

		String output = "";
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (col == size - 1) {
					output += grid[row][col];
				} else {
					output += grid[row][col] + ",";
				}
			}
			output += "\n";
		}

		// placeholder
		return output;
	} // end of toString()

	@Override
	public boolean validate() {
		boolean solutionValid = true;

		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (!validCell(row, col)) {
					solutionValid = false;
				}
			}
		}

		return solutionValid;
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

} // end of class StdSudokuGrid
