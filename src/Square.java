import processing.core.PImage;

public class Square {
	
	static int SQUARE_WIDTH = 30;
	static int SQUARE_HEIGHT = 30;
	
	private Location loc;
	private PImage image = null;
	private boolean falling;
	
	public Square(Location l) {
		loc = l;
		falling = false;
	}
	
	public Square(Location l, PImage im) {
		loc = l;
		image = im;
		falling = true;
	}
	
	public Square(Location l, PImage im, boolean b) {
		loc = l;
		image = im;
		falling = b;
	}
	
	public void move(int direction) {
		int w = Square.SQUARE_WIDTH;
		int h = Square.SQUARE_HEIGHT;
		if (direction == 0) loc = new Location(loc.getCol() + w, loc.getRow());
		if (direction == 1) loc = new Location(loc.getCol(), loc.getRow() + h);
		if (direction == 2) loc = new Location(loc.getCol() - w, loc.getRow());
		if (direction == 3) loc = new Location(loc.getCol(), loc.getRow() - h);
	}

	public boolean isFalling() {
		return falling;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public Location getGridSpot() {
		int x = (loc.getCol() - Main.LEFT_EDGE) / SQUARE_WIDTH;
		int y = (loc.getRow() - Main.TOP)/ SQUARE_HEIGHT;
		return new Location(x, y);
	}
	
	
	public PImage getImage() {
		return image;
	}
}
