import java.util.ArrayList;

import processing.core.PApplet;

public class Wall {
	
	// Walls are objects that arcs can bounce off of. They are seen as line segments
	
	public static ArrayList<Wall> walls = new ArrayList<Wall>();
	
	private static PApplet p;
	private Point point1;
	private Point point2;
	private float angle;
	
	int[] color;
	
	public Wall(float x1, float y1, float x2, float y2) {
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
		this.angle = Geo.angle(point1, point2);
		
		walls.add(this);
		color = new int[] {(int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)};
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	
	public void show() {
		p.pushStyle();
			p.stroke(color[0], color[1], color[2]);
			p.line(point1.x, point1.y, point2.x, point2.y);
		p.popStyle();
		
		/*
		Vector2 perp = this.getPerpPoint(Wave.waves.get(0).getPos());
		p.pushMatrix();
			p.fill(200, 0, 0);
			p.ellipse(perp.getX(), perp.getY(), 6, 6);
		p.popMatrix();
		*/
	}
	
	public static void showWalls() {
		for(Wall wall: walls) {
			wall.show();
		}
	}
	
	public Point getPerpPoint(Point v) {
		return Geo.perpPoint(point1, point2, v);
	}
	
	public Point getPoint1() {
		return point1;
	}
	public Point getPoint2() {
		return point2;
	}
	public Point[] getPoints() {
		return new Point[] {point1, point2};
	}
	public float getAngle() {
		return angle;
	}
}
