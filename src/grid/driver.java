package grid;

import java.io.FileNotFoundException;
import java.io.IOException;

public class driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SudokuGrid grid = new KillerSudokuGrid();
		
		try {
			grid.initGrid(args[0]);
			grid.outputGrid(args[1]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
