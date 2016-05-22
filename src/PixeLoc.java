
public class PixeLoc extends Location{

	public PixeLoc(int r, int c) {
		super(r, c);
	}
	
	public GridLoc toGrid() {
		int r = (row - Main.TOP) / Square.SQUARE_HEIGHT;
		int c = (col - Main.LEFT_EDGE) / Square.SQUARE_WIDTH;
		return new GridLoc(r, c);
		}
}
