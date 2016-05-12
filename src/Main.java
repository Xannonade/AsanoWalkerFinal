import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
	Square[][] grid = new Square[10][24];
	static ArrayList<PImage> squareImages = new ArrayList<PImage>();
	static AudioClip song;
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
			song = Applet.newAudioClip(new URL("file:" + "Assets/TetrisSong.mp3"));
			song.loop();
			System.out.println(song.toString());
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
		rect(LEFT_EDGE, TOP, RIGHT_EDGE - LEFT_EDGE, BOTTOM - TOP - 75);
		frameCount++;
		updateShape();
		updateImages();
		
		
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
		for(int i = 0; i < grid.length; i++) {
			isFull = true;
			for(int j = 0; j < grid[i].length; j++) {
				if((grid[i][j] instanceof EmptySquare)) System.out.println(grid[i][j].getGridSpot());
				if(grid[i][j].isFalling() == true || grid[i][j] instanceof EmptySquare){
					if(i == grid.length - 1) System.out.println(j);
					isFull = false;
					break;
				}
			}
			if(isFull) removeRow(i);
		}
	}

	private void removeRow(int r) {
		for (int row = 0; row < r; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				Square s = grid[row][col];
				if(s.isFalling() == false && !(s instanceof EmptySquare)) s.move(1);
			}
		}
		score += 100;
	}

	public void moveShapeDown() {
		Square[] oldSquares = currentShape.getBlocks();
		if (isColliding(1)) {
			stopMovingSquares(oldSquares);
			currentShape = null;
		} else {
			currentShape.move(1);
			updateMovingSquares(oldSquares);
		}
	}
	
	//direction of -1 = left, 1 = right
	public boolean moveShape(int direction) {
		if(direction == -1 && currentShape.getFurthestLeft().getGridSpot().getX() > 0) {
			if(isColliding(2) == false){
				currentShape.move(2);
				return true;
			}
		}
		if(direction == 1 && currentShape.getFurthestRight().getGridSpot().getX() < grid.length - 1) {
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
		while (s.getLoc().getX() >= RIGHT_EDGE) {
			currentShape.move(2);
			arr.add(s);
			s = currentShape.getFurthestRight();
		}
		//System.out.println("X: " + s.getLoc().getX() + " < " + (RIGHT_EDGE - LEFT_EDGE));
		s = currentShape.getFurthestLeft();
		while (s.getLoc().getX() <= LEFT_EDGE) {
			currentShape.move(0);
			arr.add(s);
			s = currentShape.getFurthestLeft();
		}
		//System.out.println("X: " + s.getLoc().getX() + " > " + LEFT_EDGE);
		s = currentShape.getLowest();
		while (s.getLoc().getX() >= BOTTOM) {
			currentShape.move(3);
			arr.add(s);
			s = currentShape.getLowest();
		}
		//System.out.println("Y: " + s.getLoc().getY() + " < " + (BOTTOM - TOP));
		s = currentShape.getHighest();
		while (s.getLoc().getY() <= TOP) {
			currentShape.move(1);
			arr.add(s);
			s = currentShape.getHighest();
		}
		//System.out.println("Y: " + s.getLoc().getY() + " > " + TOP);
		return arr;
	}

	public void stopMovingSquares(Square[] squares) {
		for (Square s : squares) {
			Location loc = s.getLoc();
			Location l = s.getGridSpot();
			grid[l.getX()][l.getY()] = new Square(loc, s.getImage(), false);
		}
	}

	public void updateMovingSquares(Square[] oldSquares) {
		Square[] newSquares = currentShape.getBlocks();
		for (Square s : oldSquares) {
			Location loc = s.getLoc();Location l = s.getGridSpot();
			grid[l.getX()][l.getY()] = new EmptySquare(loc);
		}
		for (Square s : newSquares) {
			Location l = s.getGridSpot();
			grid[l.getX()][l.getY()] = s;
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
		if(loc.getX() >=  RIGHT_EDGE || loc.getX() <= LEFT_EDGE) return false;
		if(loc.getY() <=  TOP || loc.getY() >= BOTTOM) return false;
		System.out.println(loc);
		return false;
	}

	public boolean isColliding(int direction) {
		Square[] arr = currentShape.getBlocks();
		int dx = 0;
		int dy = 0;
		if(direction == 0) dx = 1;
		if(direction == 1) dy = 1;
		if(direction == 2) dx = -1;
		if(direction == 3) dy = -1;
		for (Square s : arr) {
			Location l = s.getGridSpot();
			//System.out.println(s.getGridSpot() + " of " + "[" + (l.getX() + dx) + ", " + (l.getY() + dy) + "]");
			if (l.getX() + dx >= grid.length || l.getX() + dx < 0) return true;
			if (l.getY() + dy >= grid[0].length || l.getY() + dy < 0) return true;
			Square adjacent = grid[l.getX() + dx][l.getY() + dy];
			if (!(adjacent instanceof EmptySquare) && adjacent.isFalling() == false)
				return true;
		}
		return false;
	}
	
	public boolean canRotate(Square[] oldArr, Square[] newArr) {
		ArrayList<Square> potentials = new ArrayList<Square>();
		for(Square a : newArr) {
			boolean isInBoth = false;
			for(Square b : oldArr) {
				if(a.getLoc().getX() == b.getLoc().getX() && a.getLoc().getY() == b.getLoc().getY()) isInBoth = true;
			}
			if(isInBoth == false) potentials.add(a);
		}
		if(potentials.isEmpty()) return true;
		for(Square s : potentials) {
			Location l = s.getGridSpot();
			if(!(grid[l.getX()][l.getY()] instanceof EmptySquare)) {
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
				display(s);
			}
		}
	}

	public boolean display(Square s) {
		if (!(s instanceof EmptySquare)) {
			image(s.getImage(), s.getLoc().getX(), s.getLoc().getY());
			return true;
		}
		return false;
	}

	public void updateShape() {
		if (currentShape == null) {
			int type = (int) (Math.random() * 7);
			int squaresHigh = Orientation.getHeight(type);
			int squaresWide = Orientation.getWidth(type);
			currentShape = new Shape(new Location(WIDTH / 2 - (Square.SQUARE_WIDTH * squaresWide),
					(4 * Square.SQUARE_HEIGHT) - Square.SQUARE_HEIGHT * squaresHigh), type);
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
			Square[] before = currentShape.getBlocks();
			currentShape.rotate(1);
			Square[] after = currentShape.getBlocks();
			currentShape.rotate(-1);
			if(canRotate(before, after)) {
				ArrayList<Square> arr = rotateShape(1);
				updateMovingSquares(arr, before);
			}
		}
		if (k == KeyAction.DOWN) {
			fastMoving = true;
		}
		if(k == KeyAction.NOTDOWN) {
			fastMoving = false;
		}
		if (k == KeyAction.RIGHT) {
			Square[] a = currentShape.getBlocks();
			if(moveShape(1)) updateMovingSquares(a);
		}
		if (k == KeyAction.LEFT) {
			Square[] a = currentShape.getBlocks();
			if(moveShape(-1) == true) updateMovingSquares(a);
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
