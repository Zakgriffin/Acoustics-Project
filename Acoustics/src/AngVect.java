
public class AngVect implements Comparable<AngVect>{
	
	// A class for holding angle and position data, as well as references to other relative data
	
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
	
	public AngVect(Point pos, float angle, boolean isMin) {
		this(pos, angle, null, isMin);
	}

	public void setLink(AngVect linkAngVect) {
		this.linkAngVect = linkAngVect;
	}
	
	
	public String toString() {
		return "" + angle;
	}

	@Override
	public int compareTo(AngVect o) {
		if(o.angle == angle) {
			return 0;
		}
		return Float.compare(angle, o.angle);
	}
}
