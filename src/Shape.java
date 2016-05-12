public class Shape {
	
	//location of the top left square of the shape
	//The location is of the top left corner of the square made by the furthest left and highest parts of the shape
	//Therefore, square at this location may be empty
	private Location loc;
	private Orientation orientation;
	private int color;
	
	public Shape(Location l, int type) {
		orientation = new Orientation(type);
		loc = l;
		color = (int)(Math.random() * 10);
	}
	
	public Square[] getBlocks() {
		Square[] arr = new Square[4];
		Location[] list = orientation.getCurrent();
		for(int i = 0; i < list.length; i++) {
			Location l = list[i];
			arr[i] = new Square(new Location(loc.getCol() + (l.getCol() * Square.SQUARE_WIDTH), loc.getRow() + (l.getRow() * Square.SQUARE_HEIGHT)), Main.squareImages.get(color));
		}
		return arr;
	}
	
	public void moveTo(int r, int c) {
		loc = new Location(r, c);
	}
	
	public void moveTo(Location l) {
		loc = l;
	}
	
	//0 = right, 1 = down, 2 = left, 3 = up
	public void move(int direction) {
		int w = Square.SQUARE_WIDTH;
		int h = Square.SQUARE_HEIGHT;
		if(direction == 0) loc = new Location(loc.getCol() + w, loc.getRow());
		if(direction == 1) loc = new Location(loc.getCol(), loc.getRow() + h);
		if(direction == 2) loc = new Location(loc.getCol() - w, loc.getRow());
		if(direction == 3) loc = new Location(loc.getCol(), loc.getRow() - h);
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public Location getGridSpot() {
		int x = (loc.getCol() - Main.LEFT_EDGE) / Square.SQUARE_WIDTH;
		int y = (loc.getRow() - Main.TOP)/ Square.SQUARE_HEIGHT;
		return new Location(x, y);
	}
	
	public int getHeight() {
		int h = 1;
		for(Location l : orientation.getCurrent()) {
			h = Math.max(h, l.getRow());
		}
		return h;
	}
	
	public Location[] rotate(int direction) {
		return orientation.rotate(direction);
	}
	
	public Square getFurthestRight() {
		Square[] squares = getBlocks();
		Square max = squares[0];
		for(Square s : squares) {
			if(s.getLoc().getCol() > max.getLoc().getCol()) max = s;
		}
		return max;
	}
	
	public Square getFurthestLeft() {
		Square[] squares = getBlocks();
		Square min = squares[0];
		for(Square s : squares) {
			if(s.getLoc().getCol() < min.getLoc().getCol()) min = s;
		}
		return min;
	}
	
	public Square getHighest() {
		Square[] squares = getBlocks();
		Square max = squares[0];
		for(Square s : squares) {
			if(s.getLoc().getRow() > max.getLoc().getRow()) max = s;
		}
		return max;
	}
	
	public Square getLowest() {
		Square[] squares = getBlocks();
		Square min = squares[0];
		for(Square s : squares) {
			if(s.getLoc().getRow() < min.getLoc().getRow()) min = s;
		}
		return min;
	}
}
