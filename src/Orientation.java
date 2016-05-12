public class Orientation {
	static final int LEFT = -1;
	static final int RIGHT = 1;
	
	static final int TSHAPE = 0;
	static final int SQUARESHAPE = 1;
	static final int LINESHAPE = 2;
	static final int LEFTSHAPE = 3;
	static final int RIGHTSHAPE = 4;
	
	// list of locations for squares in a 'T' shape
	private static Location[][] TList = {
			{ new Location(0, 0), new Location(1, 0), new Location(1, 1), new Location(2, 0) },
			{ new Location(0, 1), new Location(1, 0), new Location(1, 1), new Location(1, 2) },
			{ new Location(0, 1), new Location(1, 0), new Location(1, 1), new Location(2, 1) },
			{ new Location(0, 0), new Location(0, 1), new Location(0, 2), new Location(1, 1) } };

	// list of locations for squares in a square shape
	private static Location[][] SList = { { new Location(0, 0), new Location(0, 1), new Location(1, 0), new Location(1, 1) } };

	// list of locations for squares in a line shape
	private static Location[][] LineList = {
			{ new Location(0, 0), new Location(0, 1), new Location(0, 2), new Location(0, 3) },
			{ new Location(0, 0), new Location(1, 0), new Location(2, 0), new Location(3, 0) } };

	// list of locations for squares in a left pointing(backwards 'L') shape
	private static Location[][] LList = {
			{ new Location(0, 2), new Location(1, 0), new Location(1, 1), new Location(1, 2) },
			{ new Location(0, 0), new Location(0, 1), new Location(1, 1), new Location(2, 1) },
			{ new Location(0, 0), new Location(0, 1), new Location(0, 2), new Location(1, 0) },
			{ new Location(0, 0), new Location(1, 0), new Location(2, 0), new Location(2, 1) } };

	// list of locations for squares in a right pointing(forewards 'L') shape
	private static Location[][] RList = {
			{ new Location(0, 0), new Location(0, 1), new Location(0, 2), new Location(1, 2) },
			{ new Location(0, 0), new Location(0, 1), new Location(1, 0), new Location(2, 0) },
			{ new Location(0, 0), new Location(1, 0), new Location(1, 1), new Location(1, 2) },
			{ new Location(0, 1), new Location(1, 1), new Location(2, 0), new Location(2, 1) } };
	
	Location[][] list;
	int index;
	
	public Orientation(int type){
		if(type == TSHAPE) this.list = TList;
		if(type == SQUARESHAPE) this.list = SList;
		if(type == LINESHAPE) this.list = LineList;
		if(type == LEFTSHAPE) this.list = LList;
		if(type == RIGHTSHAPE) this.list = RList;
		index = 0;
	};
	
	public Location[] getCurrent() {
		return list[index];
	}
	
	//direction of -1 = rotating left, direction of 1 = rotating right
	public Location[] rotate(int direction) {
		index = (index + direction) % list.length;
		return list[index];
	}

	public static int getHeight(int type) {
		if(type == TSHAPE || type == SQUARESHAPE) return 2;
		if(type == LINESHAPE || type == LEFTSHAPE) return 3;
		if(type == RIGHTSHAPE) return 4;
		return -1;
	}
	
	public static int getWidth(int type) {
		if(type == LINESHAPE) return 1;
		if(type == LEFTSHAPE || type == RIGHTSHAPE || type == SQUARESHAPE) return 2;
		if(type == TSHAPE) return 3;
		return -1;
	}
}
