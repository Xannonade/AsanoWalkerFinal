import processing.core.PImage;

public class Shape {
	
	//location of the top left square of the shape
	//The location is of the top left corner of the square made by the furthest left and highest parts of the shape
	//Therefore, square at this location may be empty
	private Location loc;
	private Orientation orientation;
	
	public Shape(Location l, int type) {
		orientation = new Orientation(type);
		loc = l;
	}
	
	public Square[] getBlocks(Square[] s) {
		Square[] arr = new Square[4];
		Location[] list = orientation.getCurrent();
		for(int i = 0; i < list.length; i++) {
			PImage image = s[i].getImage();
			Location l = list[i];
			arr[i] = new Square(new Location(loc.getX() + l.getX(), loc.getY() + l.getY()), image);
		}
		return arr;
	}
	
	public int getHeight() {
		int h = 1;
		for(Location l : orientation.getCurrent()) {
			h = Math.max(h, l.getY());
		}
		return h;
	}
	
	public void rotate(int direction) {
		orientation.rotate(direction);
	}
}
