
public class cube {
	private int cSizeX;
	private int cSizeY;
	private int Topx;
	private int Topy;
	
	public cube(int topx, int topy, int x, int y) {
		cSizeX = x;
		cSizeY = y;
		Topx = topx;
		Topy = topy;
	}
	public cube(int topx, int topy, int sizeC) {
		this(topx,topy,sizeC,sizeC);
	}
	public cube(int sizeX, int sizeY) {
		this(sizeX,sizeY,10);
	}
	public int getX() {
		return Topx;
	}
	public int getY() {
		return Topy;
	}
	public int getSizeX() {
		return cSizeX;
	}
	public int getSizeY() {
		return cSizeY;
	}
	public String toString() {
		return String.valueOf(cSizeX)+" "+String.valueOf(cSizeY)
		+" "+String.valueOf(Topx)+" "+String.valueOf(Topy);
	}
}
