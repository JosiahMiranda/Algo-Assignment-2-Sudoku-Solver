/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import java.util.LinkedList;
import java.util.List;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver {
	// TODO: Add attributes as needed.

	private Column[] columns;
	int size;

	public AlgorXSolver() {
		// TODO: any initialisation you want to implement.

	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		// TODO: your implementation of the Algorithm X solver for standard Sudoku.
		
		StdSudokuGrid stdGrid = (StdSudokuGrid) grid;
		int[][] intGrid = stdGrid.grid;
		size = stdGrid.size;
		
		
		columns = createColumns(size);
		fillInitialConstraints();
    	
//    	if (sudokuSolve(stdGrid)) {
//    		return true;
//    	}

		// placeholder
		return false;
	} // end of solve()

	public boolean sudokuSolve(StdSudokuGrid grid) {
		List<Integer> columnBackTrackIndexes = new LinkedList<Integer>();
		
		if (emptyColumns()) {
			return true;
		}
		
		

		if (sudokuSolve(grid)) {
			return true;
		} else {

		}
		
		return false;
	}
	
	public boolean emptyColumns() {
		for (int i = 0; i < 324; ++i) {
			if (!columns[i].isCovered()) {
				return false;
			}
		}
		
		return true;
	}
	
	private Column[] createColumns(int size) {
		int numCols = (size*size)*4;
		int numRows = (size*size*size);
		Column[] tempColumns = new Column[numCols];
		
		for (int col = 0; col < numCols; ++col) {
			tempColumns[col] = new Column(numRows);
		}
		
		return tempColumns;
	}
	
	private void fillInitialConstraints() {
		for (int row = 1; row <= size; ++row) {
			for (int col = 1; col <= size; ++col) {
				for (int value = 1; value <= size; ++value) {
					setCellConstraintValue(row, col, value, size);
					setRowConstraintValue(row, col, value, size);
					setColumnConstraintValue(row, col, value, size);
					setBlockConstraintValue(row, col, value, size);
				}
			}
			
		}
	}
	
	private void setCellConstraintValue(int row, int col, int valueIndex, int size) {
		System.out.println("SET CELL CONSTRAINT");
		int matrixColumnIndex = size * row - (size - col) - 1;
		int realRowIndex = size*size * (row-1) + size * (col-1) + valueIndex - 1;
		System.out.println("about to set row");
		System.out.println("SET CELL CONSTRAINT, ROW = " + row + ", COL = " + col + ", VALUE = " + valueIndex + ", SIZE = " + size);
		System.out.println("colindex = " + matrixColumnIndex + " row = " + realRowIndex);
		columns[matrixColumnIndex].setRow(realRowIndex, 1);
		System.out.println("fin");
	}
	
	private void setRowConstraintValue(int row, int col, int valueIndex, int size) {
		System.out.println("SET ROW CONSTRAINT, ROW = " + row + ", COL = " + col + ", VALUE = " + valueIndex + ", SIZE = " + size);
		int matrixColumnIndex = (size*size) + (size * (row-1)) + valueIndex - 1;
		int realRowIndex = size*size * (row-1) + size * (col-1) + valueIndex - 1;
		System.out.println("colindex = " + matrixColumnIndex + " row = " + realRowIndex);
		columns[matrixColumnIndex].setRow(realRowIndex, 1);
	}
	
	private void setColumnConstraintValue(int row, int col, int valueIndex, int size) {
		System.out.println("SET COLUMN CONSTRAINT");
		int matrixColumnIndex = 2*(size*size) + (size * (col-1)) + valueIndex - 1;
		int realRowIndex = size*size * (row-1) + size * (col-1) + valueIndex - 1;
		columns[matrixColumnIndex].setRow(realRowIndex, 1);
	}
	
	private void setBlockConstraintValue(int row, int col, int valueIndex, int size) {
		System.out.println("SET BLOCK CONSTRAINT");
		int sqrt = (int) Math.sqrt(size);
		int block = (int) ((sqrt * Math.floor((row-1)/sqrt)) + Math.ceil(col/sqrt));
		int matrixColumnIndex = 3*(size*size) + (block-1) * size + valueIndex - 1;
		int realRowIndex = size*size * (row-1) + size * (col-1) + valueIndex - 1;
		System.out.println("SET BLOCK CONSTRAINT, ROW = " + row + ", COL = " + col + ", VALUE = " + valueIndex + ", SIZE = " + size + ". BLOCK = " + block);
		System.out.println("colindex = " + matrixColumnIndex + " row = " + realRowIndex);
		columns[matrixColumnIndex].setRow(realRowIndex, 1);
	}
	
	

} // end of class AlgorXSolver

class Column {

	private boolean covered;
	private Row[] rows;
	private LinkedList<Row> deleted;

	public Column(int numRows) {
		covered = false;
		setUp(numRows);
	}

	private void setUp(int numRows) {
		rows = new Row[numRows];
		for (int i = 0; i < numRows; ++i) {
			rows[i] = new Row();
		}
		deleted = new LinkedList<Row>();
	}

	public boolean isCovered() {
		return covered;
	}

	public void setCovered(boolean covered) {
		this.covered = covered;
	}
	
	public void setRow(int index, int value) {
		rows[index].setValue(value);
	}

}

class Row {

	private boolean covered;
	private int value;

	public Row() {
		value = 0;
		covered = false;
	}

	public boolean isCovered() {
		return covered;
	}

	public void setCovered(boolean covered) {
		this.covered = covered;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
