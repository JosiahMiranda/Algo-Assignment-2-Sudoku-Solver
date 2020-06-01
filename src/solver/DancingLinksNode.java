package solver;

public class DancingLinksNode {
	// variables used for each node. Has a column and links to all directions.
	ColumnNode column;
	DancingLinksNode up, down, left, right;
	// stores real row index (matrix row index) so that the row, column, and value can be retrieved.
	int realRowIndex;

	// Default constructor.
	public DancingLinksNode(int realRowIndex) {
		left = this;
		right = this;
		up = this;
		down = this;
		
		// Storing the real row index so it's easier to get the solution
		this.realRowIndex = realRowIndex;
		
	}
	
	// Constructor with column. Calls default constructor then sets the column of this node
	public DancingLinksNode(ColumnNode column, int realRowIndex) {
		this(realRowIndex);
		this.column = column;
		
	}
	
	// This is called when we want to add a new node below this node
	public DancingLinksNode addNodeBelow(DancingLinksNode node) {
		// Make the node point down to where this pointed down to.
		node.down = down;
		// Then make that node point back up to the new node
		node.down.up = node;
		// Now make the node point up to this node
		node.up = this;
		// And this node point down to that node
		down = node;
		// Then return that node
		return node;
		
		
	}
	
	// This is called when we want to add a new node to the right of this node
	public DancingLinksNode addNodeToTheRight(DancingLinksNode node) {
		
		// Make the node point right to where this pointed right to
		node.right = right;
		// Then make that node point left to the new node
		node.right.left = node;
		// Now make the node point left to this node
		node.left = this;
		// And this node point right to that node
		right = node;
		// Then return that node
		return node;
		
	}
	
	// covering this node column wise. Meaning, a row will skip this column.
	public void coverColumnWise() {
		left.right = right;
		right.left = left;
	}
	
	// covering this node rose wise. Meaning, a column will skip this row.
	public void coverRowWise() {
		up.down = down;
		down.up = up;
	}
	
	// uncovering this node column wise.
	public void unCoverColumnWise() {
		left.right = this;
		right.left = this;
	}
	
	// uncovering this node row wise.
	public void unCoverRowWise() {
		up.down = this;
		down.up = this;
	}
	
	
}
