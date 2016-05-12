
public class Location {
	
	private int x;
	private int y;
	
	public Location(int xloc, int yloc) {
		x = xloc;
		y = yloc;
	}
	
	public int getRow() {
		return y;
	}

	public int getCol() {
		return y;
	}
	
	public String toString() {
		return("[" + x + ", " + y + "]");
	}
}
