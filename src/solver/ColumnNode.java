package solver;

public class ColumnNode extends DancingLinksNode{
	String id;
	int size;
	
	public ColumnNode(String id) {
		super(-1);
		column = this;
		this.id = id;
		size = 0;
	}
	
	public void cover() {
		
		coverColumnWise();
		
		for (DancingLinksNode row = down; row != this; row = row.down) {
			
			for (DancingLinksNode col = row.right; col != row; col = col.right) {
				
				--col.column.size;
				col.coverRowWise();
				
			}
			
		}
		
	}
	
	public void uncover() {
		
		for (DancingLinksNode row = up; row != this; row = row.up) {
			
			for (DancingLinksNode col = row.left; col != row; col = col.left) {
				
				++col.column.size;
				col.unCoverRowWise();
				
			}
			
		}
		
		unCoverColumnWise();
	}
	
}
