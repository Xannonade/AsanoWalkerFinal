import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {
	
	public enum KeyAction {
		SPACE;
	};
	
	GameState gs = GameState.OPENING;
	int frameCount;
	Shape currentShape;
	Square[][] grid = new Square[10][24];
	ArrayList<PImage> squareImages;
	AudioClip song;
	
	static int WIDTH = 600;
	static int HEIGHT = 800;
	
	public static void main(String[] args) {
		PApplet.main(new String[] { Main.class.getName()});
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
		for(File file : files) {
			String s = file.getAbsolutePath();
			if(s.contains(".png") || s.contains(".jpg")) {
				squareImages.add(loadImage(s));
				System.out.println("Loaded: " + s);
			}
		}
		try {
			song = Applet.newAudioClip(new URL("file:" + "Assets/TetrisSong.mp3"));
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
		for(int r = 0; r < grid.length; r++) {
			for(int c = 0; c < grid.length; c++) {
//				grid[r][c] = new EmptySquare()
			}
		}
		song.loop();
	}
	
	public void opening() {
		
	}
	
	public void playing() {
		background(70, 170, 70);
		frameCount++;
		updateShape();
		updateImages();
	}
	
	public void updateImages() {
		for(int r = 0; r < grid.length; r++) {
			for(int c = 0; c < grid.length; c++) {
				Square s = grid[r][c];
				PImage p = s.getImage();
				Location l = s.getLoc();
				image(p, l.getX(), l.getY());
			}
		}
	}
	
	public void updateShape() {
		if(currentShape == null) {
			int type = (int)(Math.random() * 5);
			int squaresHigh = Orientation.getHeight(type);
			currentShape = new Shape(new Location(WIDTH / 2, 0 - Square.SQUARE_HEIGHT * squaresHigh), type);
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
	}
	
	public void handleEvent(KeyAction k) {
		if(k == KeyAction.SPACE) {
			if(gs == GameState.MAINMENU) {
				gs = GameState.LOAD;
				System.out.println("Starting the game");
			}
		}
	}
	
}
