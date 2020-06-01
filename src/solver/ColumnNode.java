package solver;

public class ColumnNode extends DancingLinksNode{
	// every column node has an id and a size that can be changed.
	String id;
	int size;
	
	public ColumnNode(String id) {
		super(-1);
		column = this;
		this.id = id;
		size = 0;
	}
	
	// method to cover the column
	public void cover() {
		
		// calls the method of dancing links node
		coverColumnWise();
		
		// then proceeds to cover every row in this column
		for (DancingLinksNode row = down; row != this; row = row.down) {
			
			for (DancingLinksNode col = row.right; col != row; col = col.right) {
				// and covers that row
				--col.column.size;
				col.coverRowWise();
				
			}
			
		}
		
	}
	
	public void uncover() {
		
		// proceeds to every row in this column
		for (DancingLinksNode row = up; row != this; row = row.up) {
			
			for (DancingLinksNode col = row.left; col != row; col = col.left) {
				// uncovers them
				++col.column.size;
				col.unCoverRowWise();
				
			}
			
		}
		
		// calls method of dancing links node to uncover the column completely
		unCoverColumnWise();
		
		// strictly done in reverse order from when it covered them!
	}
	
}
