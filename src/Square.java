import processing.core.PImage;

public class Square {
	
	static int SQUARE_WIDTH = 30;
	static int SQUARE_HEIGHT = 30;
	
	private Location loc;
	private PImage image;
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
	
	public boolean isFalling() {
		return falling;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public Location getGridSpot() {
		int x = (loc.getX() - Main.LEFT_EDGE) / SQUARE_WIDTH;
		int y = (loc.getY() - Main.TOP)/ SQUARE_HEIGHT;
		return new Location(x, y);
	}
	
	
	public PImage getImage() {
		return image;
	}
}
