import processing.core.PImage;

public class Square {
	
	static int SQUARE_WIDTH = 30;
	static int SQUARE_HEIGHT = 30;
	
	private GridLoc loc;
	private PImage image = null;
	private boolean falling;
	private boolean chunkFlag = false;
	
	public Square(GridLoc l) {
		loc = l;
		falling = false;
	}
	
	public Square(GridLoc l, PImage im) {
		loc = l;
		image = im;
		falling = true;
	}
	
	public Square(GridLoc l, PImage im, boolean b) {
		loc = l;
		image = im;
		falling = b;
	}
	
	public void move(int direction) {
		if(direction == 0) loc = new GridLoc(loc.getRow(), loc.getCol() + 1);
		if(direction == 1) loc = new GridLoc(loc.getRow() + 1, loc.getCol());
		if(direction == 2) loc = new GridLoc(loc.getRow(), loc.getCol() - 1);
		if(direction == 3) loc = new GridLoc(loc.getRow() - 1, loc.getCol());
	}

	public boolean isFalling() {
		return falling;
	}
	
	public GridLoc getLoc() {
		return loc;
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
