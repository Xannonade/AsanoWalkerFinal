import processing.core.PImage;

public class Square {
	
	static int SQUARE_WIDTH = 30;
	static int SQUARE_HEIGHT = 30;
	
	private Location loc;
	private PImage image;
	private boolean isFalling;
	
	public Square(Location l, PImage im) {
		loc = l;
		image = new PImage(0, 0, 255);
		isFalling = true;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public PImage getImage() {
		return image;
	}
}
