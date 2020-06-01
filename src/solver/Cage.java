package solver;

public class Cage {
	
	// class for the killer cages in killer sudoku.
	// has a sum and then all of the tuples, so cells inside of the cage.

	public int sum;
	public Tuple[] tuples;

	public Cage(int sum, Tuple[] tuples) {
		this.sum = sum;
		this.tuples = tuples;
	}
	
	public String toString() {
		String output = sum + " ";
		
		for (Tuple tuple : tuples) {
			output += tuple.row + "," + tuple.col + " ";
		}
		
		return output;
	}

}