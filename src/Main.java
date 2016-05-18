import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

public class Main extends PApplet {

	public enum KeyAction {
		SPACE, UP, DOWN, RIGHT, LEFT, NOTUP, NOTDOWN, NOTRIGHT, NOTLEFT;
	};

	GameState gs = GameState.OPENING;
	int frameCount;
	Shape currentShape;
	Square[][] grid = new Square[24][10];
	static ArrayList<PImage> squareImages = new ArrayList<PImage>();
	static AudioClip song = null;
	boolean fastMoving = false;
	int score = 0;

	static int WIDTH = 600;
	static int HEIGHT = 1000;
	static int RIGHT_EDGE = WIDTH - 150;
	static int LEFT_EDGE = 150;
	static int TOP = 25;
	static int BOTTOM = HEIGHT - 175;
	static int INITIAL_HEIGHT = TOP - 4 * Square.SQUARE_HEIGHT;

	public static void main(String[] args) {
		PApplet.main(new String[] { Main.class.getName() });
	}

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void setup() {
		frameRate(30);
		loadFiles();
	}

	public void loadFiles() {
		File[] files = new File("Assets/Images").listFiles();
		for (File file : files) {
			String s = file.getAbsolutePath();
			if (s.contains(".png") || s.contains(".jpg")) {
				squareImages.add(loadImage(s));
				System.out.println("Loaded: " + s);
			}
		}
		try {
			song = Applet.newAudioClip(new URL("file:" + "Assets/TetrisSong.wav"));
			song.loop();
			System.out.println("Loaded the song");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public enum GameState {
		OPENING(0), MAINMENU(1), LOAD(2), PLAYING(3), PAUSED(4), GAMEOVER(5), OPTIONS(6), WIN(7);

		private int value;

		private GameState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public void draw() {
		// System.out.println("Draw Check: +");
		switch (gs) {
		case OPENING:
			opening();
			System.out.println("Opening Check: +");
			gs = GameState.MAINMENU;
			break;

		case MAINMENU:
			mainMenu();
			// System.out.println("Main Menu Check: +");
			break;

		case LOAD:
			load();
			gs = GameState.PLAYING;
			break;

		case PLAYING:
			playing();
			// System.out.println("Playing Check: +");
			break;

		case PAUSED:
			paused();
			break;

		case OPTIONS:
			options();
			// System.out.println("Options Check: +");
			break;

		case GAMEOVER:
			gameOver();
			// System.out.println("Game Over Check: +");
			break;

		case WIN:
			win();
			// System.out.println("Win Check: +");
			break;
		}
	}

	public void mainMenu() {
		text("Main Menu", 10, 30);
	}

	public void load() {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				grid[r][c] = new EmptySquare(new Location(TOP + r * Square.SQUARE_HEIGHT, LEFT + c * Square.SQUARE_WIDTH));
			}
		}
		song.loop();
	}

	public void opening() {

	}

	public void playing() {
		background(70, 170, 70);
		text("Score: " + score, RIGHT_EDGE - 50, BOTTOM - 50);
		rect(LEFT_EDGE, TOP, RIGHT_EDGE - LEFT_EDGE, BOTTOM - TOP - 80);
		frameCount++;
		updateShape();
		updateImages();
		
		//System.out.println(currentShape.getGridSpot());
		
		if(frameCount % 2 == 0) {
			if(fastMoving == true) moveShapeDown();
			checkForFullRows();
		}
		
		if (frameCount % 6 == 0 && fastMoving == false) {
			moveShapeDown();
		}
	}

	private void checkForFullRows() {
		boolean isFull = true;
		for(int r = 0; r < grid.length; r++) {
			isFull = true;
			for(int c = 0; c < grid[0].length; c++) {
				//if((grid[i][j] instanceof EmptySquare)) System.out.println(grid[i][j].getGridSpot());
				//System.out.println("[" + r + ", " + c + "]");
				if(isFull == true && (grid[r][c].isFalling() == true || grid[r][c].getImage() == null)){
					// System.out.println(c);
					isFull = false;
				}
			}
			if(isFull) removeRow(r);
		}
	}
	
	private void removeRow(int r) {
		//empty the completed column
		for(int col = 0; col < grid[0].length; col++) {
			grid[r][col] = new EmptySquare(new Location(r, col));
		}
		
		//get score
		score += 100;
		
		//move floating blocks down (using chunk method)
		moveFloatersDown();
		
		
	}

	/*
	 * after a row is removed, some squares may be floating
	 * this method will run after the completed row is cleared
	 * all floating blocks will be grouped into 'chunks' of touching blocks
	 * all 'chunks' will fall as individual shapes
	 */
	private void moveFloatersDown() {
		ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
		for (int r = 0; r < grid.length; r++)
			for (int c = 0; c < grid[0].length; c++) {
				Location l = new Location(r, c);
				if (!checkIfChunkIsMade(chunkList, l))
					chunkList.add(createChunk(l));
			}
		//make the chunks fall
		letChunksFall(chunkList);
		//loop thru all and set flags to false
		resetFlags();
	}
	
	private void letChunksFall(ArrayList<Chunk> chunkList) {
		for(Chunk currentChunk : chunkList) {
			System.out.println(currentChunk.getSquareList().size());
			boolean canFall = true;
			while (canFall == true) {
				for (Square s : currentChunk.getSquareList()) {
					Location loc = s.getGridSpot();
					if (!isInside(new Location(loc.getRow() + 1, loc.getCol())) || grid[loc.getRow() + 1][loc.getCol()] instanceof EmptySquare) {
						canFall = false;
						break;
					}
					System.out.println(canFall);
					if (canFall) currentChunk.moveDown();
				}
			}
		}
	}

	private void resetFlags() {
		for(int r = 0; r < grid.length; r++) {
			for(int c = 0; c < grid[r].length; c++) {
				grid[r][c].flag(false);
			}
		}
	}

	/*
	 * list is the chunks (in the form of Shape) that are already made
	 * this checks to see if the specific block has already been made into a chunk earlier
	 */
	private boolean checkIfChunkIsMade(ArrayList<Chunk> list, Location l) {
		for (Chunk chunk : list)
			for (Square square : chunk.getSquareList()){
				System.out.println("yo");
				if (square.getLoc().equals(l))
					return true;
			}
		return false;
	}

	private Chunk createChunk(Location l) {
		Chunk finalChunk = new Chunk();
		//make one chunk recursively
		recursiveSquareAdd(l, finalChunk);
		return finalChunk;
	}

	private void recursiveSquareAdd(Location l, Chunk finalChunk) {
		Square s = grid[l.getRow()][l.getCol()];
		
		//base case
		if (!isInside(s) || s.getFlag() || s instanceof EmptySquare) return;
		
		finalChunk.add(s);
		System.out.println(s.getGridSpot());
		recursiveSquareAdd(new Location(l.getRow() + 1, l.getCol()), finalChunk);
		recursiveSquareAdd(new Location(l.getRow(), l.getCol() + 1), finalChunk);
		recursiveSquareAdd(new Location(l.getRow() - 1, l.getCol()), finalChunk);
		recursiveSquareAdd(new Location(l.getRow(), l.getCol() - 1), finalChunk);
	}

	public void moveShapeDown() {
		Square[] oldSquares = currentShape.getBlocks();
		if (isColliding(1) == true) {
			stopMovingSquares(oldSquares);
			currentShape = null;
		} else {
			currentShape.move(1);
			updateMovingSquares(oldSquares);
		}
	}
	
	//direction of -1 = left, 1 = right
	public boolean moveShape(int direction) {
		if(direction == -1 && currentShape.getFurthestLeft().getGridSpot().getCol() > 0) {
			if(isColliding(2) == false){
				currentShape.move(2);
				return true;
			}
		}
		if(direction == 1 && currentShape.getFurthestRight().getGridSpot().getCol() < grid[0].length - 1) {
			if(isColliding(0) == false) {
				currentShape.move(0);
				return true;
			}
		}
		return false;
	}

	public ArrayList<Square> rotateShape(int direction) {
		ArrayList<Square> arr = new ArrayList<Square>();
		currentShape.rotate(direction);
		Square s = currentShape.getFurthestRight();
		while (s.getLoc().getCol() >= RIGHT_EDGE) {
			currentShape.move(2);
			arr.add(s);
			s = currentShape.getFurthestRight();
		}
		//System.out.println("X: " + s.getLoc().getCol() + " < " + (RIGHT_EDGE - LEFT_EDGE));
		s = currentShape.getFurthestLeft();
		while (s.getLoc().getCol() <= LEFT_EDGE) {
			currentShape.move(0);
			arr.add(s);
			s = currentShape.getFurthestLeft();
			System.out.println(s.getLoc().getCol() + " of " + LEFT_EDGE);
		}
		//System.out.println("X: " + s.getLoc().getCol() + " > " + LEFT_EDGE);
		s = currentShape.getLowest();
		while (s.getLoc().getRow() >= BOTTOM) {
			currentShape.move(3);
			arr.add(s);
			s = currentShape.getLowest();
		}
		//System.out.println("Y: " + s.getLoc().getRow() + " < " + (BOTTOM - TOP));
		s = currentShape.getHighest();
		while (s.getLoc().getRow() <= TOP) {
			currentShape.move(1);
			arr.add(s);
			s = currentShape.getHighest();
		}
		//System.out.println("Y: " + s.getLoc().getRow() + " > " + TOP);
		return arr;
	}

	public void stopMovingSquares(Square[] squares) {
		for (Square s : squares) {
			Location loc = s.getLoc();
			Location l = s.getGridSpot();
			grid[l.getRow()][l.getCol()] = new Square(loc, s.getImage(), false);
		}
	} 

	public void updateMovingSquares(Square[] oldSquares) {
		Square[] newSquares = currentShape.getBlocks();
		for (Square s : oldSquares) {
			Location loc = s.getLoc();
			Location l = s.getGridSpot();
			grid[l.getRow()][l.getCol()] = new EmptySquare(loc);
		}
		for (Square s : newSquares) {
			Location l = s.getGridSpot();
			grid[l.getRow()][l.getCol()] = s;
		}
	}
	
	public void updateMovingSquares(ArrayList<Square> arr, Square[] oldSquares) {
		for(int i = 0; i < arr.size(); i++) {
			if(isInside(arr.get(i)) == false) arr.remove(i);
		}
		Square[] a = new Square[arr.size() + oldSquares.length];
		for(int i = 0; i < arr.size() + oldSquares.length; i++) {
			if(i < arr.size()) {
				a[i] = arr.get(i);
			} else {
				a[i] = oldSquares[i - arr.size()];
			}
			//System.out.println(a[i].getLoc());
		}
		updateMovingSquares(a);
	}

	private boolean isInside(Square s) {
		Location loc = s.getLoc();
		if(loc.getCol() >=  RIGHT_EDGE || loc.getCol() <= LEFT_EDGE) return false;
		if(loc.getRow() <=  TOP || loc.getRow() >= BOTTOM) return false;
		return false;
	}
	
	private boolean isInside(Location loc) {
		if(loc.getCol() >  grid[0].length - 1|| loc.getCol() < 0) return false;
		if(loc.getRow() <  0 || loc.getRow() > grid.length - 1) return false;
		return false;
	}	

	public boolean isColliding(int direction) {
		Square[] arr = currentShape.getBlocks();
		int dr = 0;
		int dc = 0;
		if(direction == 0) dc = 1;
		if(direction == 1) dr = 1;
		if(direction == 2) dc = -1;
		if(direction == 3) dr = -1;
		for (Square s : arr) {
			Location l = s.getGridSpot();
			//System.out.println(s.getGridSpot() + " of " + "[" + (l.getRow() + dx) + ", " + (l.getCol() + dy) + "]");
			if (l.getRow() + dr >= grid.length || l.getRow() + dr < 0) {
				return true;
			}
			if (l.getCol() + dc >= grid[0].length|| l.getCol() + dc < 0) {
				return true;
			}
			Square adjacent = grid[l.getRow() + dr][l.getCol() + dc];
			if (!(adjacent instanceof EmptySquare) && adjacent.isFalling() == false) return true;
		}
		return false;
	}
	
	public boolean canRotate(Square[] oldArr, Square[] newArr) {
		ArrayList<Square> potentials = new ArrayList<Square>();
		for(Square a : newArr) {
			boolean isInBoth = false;
			for(Square b : oldArr) {
				if(a.getLoc().getRow() == b.getLoc().getRow() && a.getLoc().getCol() == b.getLoc().getCol()) isInBoth = true;
			}
			if(isInBoth == false) potentials.add(a);
		}
		if(potentials.isEmpty()) return true;
		for(Square s : potentials) {
			Location l = s.getGridSpot();
			if(isInside(s) && !(grid[l.getRow()][l.getCol()] instanceof EmptySquare)) {
				//System.out.println("false");
				return false;
			}
		}
		return true;
	}

	public void updateImages() {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				Square s = grid[r][c];
				//if(!(s instanceof EmptySquare)) System.out.println(r + ", " + c);
				display(s);
			}
		}
	}

	public boolean display(Square s) {
		if (!(s instanceof EmptySquare)) {
			image(s.getImage(), s.getLoc().getCol(), s.getLoc().getRow());
			return true;
		}
		return false;
	}

	public void updateShape() {
		if (currentShape == null) {
			int type = (int) (Math.random() * 7);
			int squaresHigh = Orientation.getHeight(type);
			int squaresWide = Orientation.getWidth(type);
			currentShape = new Shape(new Location(TOP + (4 - squaresHigh) * Square.SQUARE_HEIGHT, WIDTH / 2 - (Square.SQUARE_WIDTH * squaresWide)), type);
			System.out.println("Created a new shape");
		}
	}

	public void paused() {
		song.stop();
	}

	public void options() {

	}

	public void gameOver() {
		song.stop();
	}

	public void win() {
		song.stop();
	}

	public void keyPressed() {
		if (keyCode == ' ')
			handleEvent(KeyAction.SPACE);
		if (keyCode == UP)
			handleEvent(KeyAction.UP);
		if (keyCode == DOWN)
			handleEvent(KeyAction.DOWN);
		if (keyCode == RIGHT)
			handleEvent(KeyAction.RIGHT);
		if (keyCode == LEFT)
			handleEvent(KeyAction.LEFT);
	}

	public void keyReleased(KeyEvent e) {
		if (keyCode == UP)
			handleEvent(KeyAction.NOTUP);
		if (keyCode == DOWN)
			handleEvent(KeyAction.NOTDOWN);
		if (keyCode == RIGHT)
			handleEvent(KeyAction.NOTRIGHT);
		if (keyCode == LEFT)
			handleEvent(KeyAction.NOTLEFT);
	}

	public void handleEvent(KeyAction k) {
		if (k == KeyAction.SPACE) {
			if (gs == GameState.MAINMENU) {
				gs = GameState.LOAD;
				System.out.println("Starting the game");
			}
		}
		if (k == KeyAction.UP) {
			if (currentShape != null) {
				Square[] before = currentShape.getBlocks();
				currentShape.rotate(1);
				Square[] after = currentShape.getBlocks();
				currentShape.rotate(-1);
				if (canRotate(before, after)) {
					ArrayList<Square> arr = rotateShape(1);
					updateMovingSquares(arr, before);
				}
			}
		}
		if (k == KeyAction.DOWN) {
			fastMoving = true;
		}
		if(k == KeyAction.NOTDOWN) {
			fastMoving = false;
		}
		if (k == KeyAction.RIGHT) {
			if(currentShape != null) {
				Square[] a = currentShape.getBlocks();
				if(moveShape(1)) updateMovingSquares(a);
			}
		}
		if (k == KeyAction.LEFT) {
			if(currentShape != null) {
				Square[] a = currentShape.getBlocks();
				if(moveShape(-1) == true) updateMovingSquares(a);
			}
		}
	}

	// Image index
	// blue 0
	// brown 1
	// dark grey 2
	// green 3
	// grey 4
	// orange 5
	// purple 6
	// red 7
	// teal 8
	// yellow 9

}
