
public class Point {
	float x;
	float y;
	
	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	Point() {
	
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public void addX(float x) {
		this.x += x;
	}
	public void addY(float y) {
		this.y += y;
	}
	
	public float slopeWith(Point v) {
		return (y - v.getY()) / (x - v.getX());
	}
	
	public float angleTo(Point v) {
		double angle = Math.atan2(v.getY() - y, v.getX() - x);
		if(angle < 0) angle += Math.PI * 2;
		return (float) angle;
	}

	public float distanceTo(Point pos) {
		float xDif = pos.getX() - x;
		float yDif = pos.getY() - y;
		
		return (float) Math.sqrt(xDif * xDif + yDif * yDif);
	}

	public Point boost(Point p2) {
		return new Point(p2.getX() * 2 - x, p2.getY() * 2 - y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
