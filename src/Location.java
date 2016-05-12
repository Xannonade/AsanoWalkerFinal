
public class Location {
	
	private int x;
	private int y;
	
	public Location(int xloc, int yloc) {
		x = xloc;
		y = yloc;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		return("[" + x + ", " + y + "]");
	}
}
