
public class AngVect implements Comparable<AngVect>{
	public float angle;
	public Point pos;
	public AngVect linkAngVect;
	boolean isMin;
	public Wall wall;
	
	public AngVect(Point pos, float angle, Wall wall, boolean isMin) {
		this.pos = pos;
		this.angle = angle;
		this.wall = wall;
		this.isMin = isMin;
	}

	public void setLink(AngVect linkAngVect) {
		this.linkAngVect = linkAngVect;
	}
	
	
	public String toString() {
		return "" + angle;
	}

	@Override
	public int compareTo(AngVect o) {
		return Float.compare(angle, o.angle);
	}
}
