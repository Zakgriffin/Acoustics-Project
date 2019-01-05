
public class AngVect implements Comparable<AngVect>{
	
	// A class for holding angle and position data, as well as references to other relative data
	
	public float angle;
	public Point pos;
	public AngVect link;
	boolean isMin;
	public Wall wall;
	
	public AngVect(Point pos, float angle, Wall wall, boolean isMin) {
		this.pos = pos;
		this.angle = angle;
		this.wall = wall;
		this.isMin = isMin;
	}
	
	public String toString() {
		return "" + angle;
	}

	@Override
	public int compareTo(AngVect o) {
		return Float.compare(angle, o.angle);
	}
}
