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
	
	public boolean isInSameChunk(Square s) {
		for(Square S : list) {
			if(s.getLoc().getRow() == S.getLoc().getRow() && s.getLoc().getCol() == S.getLoc().getCol()) return true;
		}
		return false;
	}
}