/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;

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
	
	private int[][] grid;
	private int dimension;
	private int[] values;
	
	
	

	public StdSudokuGrid() {
		super();

		// TODO: any necessary initialisation at the constructor
	} // end of StdSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String line;
			
			line = reader.readLine();
			dimension = Integer.parseInt(line);
			
			
			dimension = 9;
			grid = new int[dimension][dimension];
			

		} catch (FileNotFoundException ex) {
			System.err.println(String.format("File: %s not found.", filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} // end of initBoard()
	

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		// TODO
	} // end of outputBoard()

	@Override
	public String toString() {
		// TODO

		// placeholder
		return String.valueOf("");
	} // end of toString()

	@Override
	public boolean validate() {
		// TODO

		// placeholder
		return false;
	} // end of validate()

} // end of class StdSudokuGrid
