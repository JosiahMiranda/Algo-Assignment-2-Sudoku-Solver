/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;

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
		try {
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

			line = reader.readLine();
			int numCages = Integer.parseInt(line);
			cages = new Cage[numCages];

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
					tuples[i - 1] = tuple;
				}
				Cage cage = new Cage(sum, tuples);
				cages[counter] = cage;
				++counter;
			}

			reader.close();
		} catch (FileNotFoundException ex) {
			System.err.println(String.format("File: %s not found.", filename));
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}

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
				output += grid[row][col];
			}
			output += "\n";
		}

		// placeholder
		return output;
	} // end of toString()

	@Override
	public boolean validate() {
		// TODO
		// I think this only really works for backtracking so far. Though not sure, will
		// have to check
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (grid[row][col] == 0) {
					return false;
				}
			}
		}

		return true;
	} // end of validate()

} // end of class KillerSudokuGrid

// Inner class for cages
class Cage {

	public int sum;
	public Tuple[] tuples;

	public Cage(int sum, Tuple[] tuples) {
		this.sum = sum;
		this.tuples = tuples;
	}

} // end of inner class Cage

// Inner class for tuples

class Tuple {

	public int row;
	public int col;

	public Tuple(int row, int col) {
		this.row = row;
		this.col = col;
	}
} // end of inner class Tuple
