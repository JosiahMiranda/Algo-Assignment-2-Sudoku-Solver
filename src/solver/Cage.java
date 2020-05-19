package solver;

public class Cage {

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