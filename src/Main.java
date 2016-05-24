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
	ArrayList<Chunk> chunkList;

	static int WIDTH = 600;
	static int HEIGHT = 800;
	static int RIGHT_EDGE = WIDTH - 150;
	static int LEFT_EDGE = 150;
	static int TOP = 25;
	static int BOTTOM = HEIGHT - 55;
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
		background(0, 255, 255);
		textSize(50);
		text("Main Menu", 150, TOP + 50);
	}

	public void load() {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				grid[r][c] = new EmptySquare(new GridLoc(r, c));
			}
		}
		song.loop();
	}

	public void opening() {

	}

	public void playing() {
		background(70, 170, 70);
		textSize(26);
		text("Score: " + score, RIGHT_EDGE - 100, BOTTOM + 30);
		rect(LEFT_EDGE, TOP, RIGHT_EDGE - LEFT_EDGE, BOTTOM - TOP);
		frameCount++;
		updateShape();
		updateImages();
		
		//System.out.println(currentShape.getGridSpot());
		
		if(frameCount % 2 == 0) {
			checkIfLost();
			if(fastMoving == true) moveShapeDown();
			checkForFullRows();
		}
		
		if (frameCount % 6 == 0 && fastMoving == false) {
			moveShapeDown();
		}
	}
	
	public void checkIfLost() {
		for(int c = 0; c < grid[0].length; c++) {
			if(!(grid[0][c] instanceof EmptySquare)) {
				song.stop();
				gs = GameState.GAMEOVER;
				break;
			}
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
			grid[r][col] = new EmptySquare(new GridLoc(r, col));
		}
		
		//get score
		score += 100;
		
		//move floating blocks down (using chunk method)
		moveFloatersDown(r);
		
		
	}

	/*
	 * after a row is removed, some squares may be floating
	 * this method will run after the completed row is cleared
	 * all floating blocks will be grouped into 'chunks' of touching blocks
	 * all 'chunks' will fall as individual shapes
	 */
	private void moveFloatersDown(int row) {
		chunkList = new ArrayList<Chunk>();
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[0].length; c++) {
				Square s = grid[r][c];
				GridLoc l = new GridLoc(r, c);
				if (!(s instanceof EmptySquare) && !s.getFlag()) createChunk(l);
			}
		}
		// make the chunks fall
		letChunksFall();
		// loop thru all and set flags to false
		resetFlags();
		chunkList.clear();
	}

	private void letChunksFall() {
		if (chunkList.size() > 0)
			for (Chunk currentChunk : chunkList) {
				System.out.println(currentChunk.getSquareList().size());
				boolean canFall = true;
				while (canFall == true) {
					inner: for (Square s : currentChunk.getSquareList()) {
						GridLoc loc = s.getLoc();
						if (!isInside(new GridLoc(loc.getRow() + 1, loc.getCol()))) {
							canFall = false;
							break inner;
						}
						//should be in the same chunk, not just flagged
						if (!(grid[loc.getRow() + 1][loc.getCol()] instanceof EmptySquare)  && grid[loc.getRow() + 1][loc.getCol()].getFlag() == false && currentChunk.isInSameChunk(s)) {
							canFall = false;
							break inner;
						}
					}
					if (canFall) {
						Chunk c = currentChunk;
						currentChunk.moveDown();
						for (Square a : c.getSquareList()) {
							boolean isInBoth = false;
							for (Square b : currentChunk.getSquareList()) {
								if (a.getLoc().equals(b.getLoc()))
									isInBoth = true;
							}
							if (isInBoth == false) {
								GridLoc l = a.getLoc();
								grid[l.getRow()][l.getCol()] = new EmptySquare(l);
							}
						}
					}
				}
			}
	}

	private void resetFlags() {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				if (grid[r][c].getFlag()) {
					Square s = grid[r][c];
					s.flag(false);
					if (isInside(s) && !(s instanceof EmptySquare)) {
						GridLoc loc = s.getLoc();
						grid[r][c] = new EmptySquare(loc);
						grid[loc.getRow()][loc.getCol()] = s;
					}
				}
			}
		}
	}

	private Chunk createChunk(GridLoc l) {
		chunkList.add(new Chunk());
		//make one chunk recursively
		recursiveSquareAdd(l, chunkList.size() - 1);
		return chunkList.get(chunkList.size() - 1);
	}

	private void recursiveSquareAdd(GridLoc l, int index) {
		//base case
		if (!isInside(l)) {
			return;
		}
		Square s = grid[l.getRow()][l.getCol()];
		if(s instanceof EmptySquare) {
			return;
		}
		if(s.getFlag()) {
			return;
		}
		
		s.flag(true);
		chunkList.get(index).add(s);
		recursiveSquareAdd(new GridLoc(l.getRow() + 1, l.getCol()), index);
		recursiveSquareAdd(new GridLoc(l.getRow(), l.getCol() + 1), index);
		recursiveSquareAdd(new GridLoc(l.getRow() - 1, l.getCol()), index);
		recursiveSquareAdd(new GridLoc(l.getRow(), l.getCol() - 1), index);
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
		if(direction == -1 && currentShape.getFurthestLeft().getLoc().getCol() > 0) {
			if(isColliding(2) == false){
				currentShape.move(2);
				return true;
			}
		}
		if(direction == 1 && currentShape.getFurthestRight().getLoc().getCol() < grid[0].length - 1) {
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
		while (s.getLoc().getCol() >= grid[0].length - 1) {
			currentShape.move(2);
			arr.add(s);
			s = currentShape.getFurthestRight();
		}
		s = currentShape.getFurthestLeft();
		while (s.getLoc().getCol() <= 0) {
			currentShape.move(0);
			arr.add(s);
			s = currentShape.getFurthestLeft();
		}
		s = currentShape.getLowest();
		while (s.getLoc().getRow() >= grid.length - 1) {
			currentShape.move(3);
			arr.add(s);
			s = currentShape.getLowest();
		}
		s = currentShape.getHighest();
		while (s.getLoc().getRow() <= 0) {
			currentShape.move(1);
			arr.add(s);
			s = currentShape.getHighest();
		}
		return arr;
	}

	public void stopMovingSquares(Square[] squares) {
		for (Square s : squares) {
			GridLoc loc = s.getLoc();
			grid[loc.getRow()][loc.getCol()] = new Square(loc, s.getImage(), false);
		}
	} 

	public void updateMovingSquares(Square[] oldSquares) {
		Square[] newSquares = currentShape.getBlocks();
		for (Square s : oldSquares) {
			GridLoc loc = s.getLoc();
			grid[loc.getRow()][loc.getCol()] = new EmptySquare(loc);
		}
		for (Square s : newSquares) {
			GridLoc l = s.getLoc();
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
		GridLoc loc = s.getLoc();
		if(loc.getCol() >=  grid[0].length) {
			System.out.println("A");
			return false;
		}
		if(loc.getCol() <= 0) {
			System.out.println("B");
			return false;
		}
		if(loc.getRow() <= 0) {
			System.out.println("C");
			return false;
		}
		if(loc.getRow() >= grid.length) {
			System.out.println("D");
			return false;
		}
		return true;
	}
	
	private boolean isInside(GridLoc loc) {
		if(loc.getCol() >  grid[0].length - 1 || loc.getCol() < 0) return false;
		if(loc.getRow() <  0 || loc.getRow() > grid.length - 1) return false;
		return true;
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
			GridLoc l = s.getLoc();
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
				if(a.getLoc().equals(b.getLoc())) isInBoth = true;
			}
			if(isInBoth == false) potentials.add(a);
		}
		if(potentials.isEmpty()) return true;
		for(Square s : potentials) {
			GridLoc l = s.getLoc();
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
			PixeLoc l = s.getLoc().toPixels();
			image(s.getImage(), l.getCol(), l.getRow());
			return true;
		}
		return false;
	}

	public void updateShape() {
		if (currentShape == null) {
			int type = (int) (Math.random() * 7);
			makeShape(type);
			System.out.println("Created a new shape");
		}
	}
	
	public void makeShape(int type) {
		int squaresHigh = Orientation.getHeight(type);
		int squaresWide = Orientation.getWidth(type);
		currentShape = new Shape(new GridLoc(4 - squaresHigh, grid[0].length / 2 - squaresWide), type);
		while(shapeOverlaps()) {
			currentShape.move(3);
		}
	}
	
	public boolean shapeOverlaps() {
		for(Square s : currentShape.getBlocks()) {
			if(currentShape.getLoc().getRow() < 0) {
				song.stop();
				gs = GameState.GAMEOVER;
			}
			GridLoc loc = s.getLoc();
			Square sq = grid[loc.getRow()][loc.getCol()];
			if(!(sq instanceof EmptySquare) && sq.isFalling() == false) return true;
		}
		return false;
	}

	public void paused() {
		song.stop();
	}

	public void options() {

	}

	public void gameOver() {
		background(0, 0, 0);
		textSize(40);
		text("GAME OVER", WIDTH / 2 - 115, 100);
		textSize(25);
		text("You lose, loser!", WIDTH / 2 - 90, 150);
		text("Final Score: " + score, WIDTH / 2 - 85, 250);
		textSize(15);
		text("Press space for main menu.", WIDTH / 2 - 90, HEIGHT - 150);
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
			if (gs == GameState.GAMEOVER) {
				gs = GameState.MAINMENU;
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
