
public class GridLoc extends Location{

	public GridLoc(int r, int c) {
		super(r, c);
	}
	
	public PixeLoc toPixels() {
		int r = Square.SQUARE_HEIGHT * row + Main.TOP;
		int c = Square.SQUARE_WIDTH * col + Main.LEFT_EDGE;
		return new PixeLoc(r, c);
	}
}
