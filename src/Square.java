import processing.core.PImage;

public class Square {
	
	static int SQUARE_WIDTH = 30;
	static int SQUARE_HEIGHT = 30;
	
	private Location loc;
	private PImage image = null;
	private boolean falling;
	private boolean chunkFlag = false;
	
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
		if(direction == 0) loc = new Location(loc.getRow(), loc.getCol() + w);
		if(direction == 1) loc = new Location(loc.getRow() + h, loc.getCol());
		if(direction == 2) loc = new Location(loc.getRow(), loc.getCol() - w);
		if(direction == 3) loc = new Location(loc.getRow() - h, loc.getCol());
	}

	public boolean isFalling() {
		return falling;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public Location getGridSpot() {
		int r = (loc.getRow() - Main.TOP)/ SQUARE_HEIGHT;
		int c = (loc.getCol() - Main.LEFT_EDGE) / SQUARE_WIDTH;
		return new Location(r, c);
	}
	
	public void flag(boolean f) {
		chunkFlag = f;
	}
	
	public boolean getFlag() {
		return chunkFlag;
	}
	
	public PImage getImage() {
		return image;
	}
}
