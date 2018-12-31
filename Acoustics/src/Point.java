public class Point {
	
	// A simple class for points. Can hold and do basic things with x and y values
	
	public float x;
	public float y;
	
	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void addX(float x) {
		this.x += x;
	}
	public void addY(float y) {
		this.y += y;
	}
	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void set(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	
	public float slopeWith(Point p) {
		return Geo.slope(this, p);
	}
	
	public float angleTo(Point p) {
		return Geo.angle(this, p);
	}

	public float distanceTo(Point p) {
		return Geo.distance(this, p);
	}

	public Point boost(Point p) {
		return Geo.boost(this, p);
	}
	
	public boolean fallsWithin(Point p1, Point p2) {
		return Geo.pointFallsWithin(this, p1, p2);
	}
	public boolean fallsWithin(Point[] points) {
		return fallsWithin(points[0], points[1]);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
