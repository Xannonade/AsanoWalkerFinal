import java.util.ArrayList;

public class Chunk {
	private ArrayList<Square> list;
	
	public Chunk() {
		list = new ArrayList<Square>();
	}
	
	public ArrayList<Square> getSquareList() {
		return list;
	}
	
	public void add(Square s) {
		list.add(s);
	}
	
	public void moveDown() {
		for(Square s : list) {
			s.move(1);
		}
	}
}