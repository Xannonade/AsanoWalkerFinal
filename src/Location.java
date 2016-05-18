
public class Location {
	
	private int row;
	private int col;
	
	public Location(int r, int c) {
		row = r;
		col = c;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public boolean equals(Location other) {
		if (row == other.getRow())
			if (col == other.getCol())
				return true;
		return false;
	}
	
	public String toString() {
		return("[" + row + ", " + col + "]");
	}
}
