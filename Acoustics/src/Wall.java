import java.util.ArrayList;

import processing.core.PApplet;

public class Wall {
	public static ArrayList<Wall> walls = new ArrayList<Wall>();
	
	private static PApplet p;
	private Point point1;
	private Point point2;
	
	private float minAngle;
	private float maxAngle;
	private Point minPoint;
	private Point maxPoint;
	
	int[] color;
	
	public Wall(float x1, float y1, float x2, float y2) {
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
		
		walls.add(this);
		color = new int[] {(int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)};
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	
	public void show() {
		p.pushStyle();
			p.stroke(color[0], color[1], color[2]);
			p.line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
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
		float x = point1.getX();
		float y = point1.getY();
		
		float px;
		float py;
		if(point1.getY() == point2.getY()) {
			// horizontal line protection
			px = v.getX();
			py = y;
		} else if(point1.getX() == point2.getX()) {
			// vertical line protection
			px = x;
			py = v.getY();
		} else {
			float m1 = point1.slopeWith(point2);
			float m2 = 1 / -m1;
			px = (m2 * v.getX() - v.getY() + y - m1 * x) / (m2 - m1);
			py = m1 * (px - x) + y;
		}
		return new Point(px, py);
	}

	public void updateToPoint(Point p) {
		if((p.angleTo(point1) - p.angleTo(point2)) > Math.PI ) {
			minPoint = point1;
			maxPoint = point2;
		} else {
			minPoint = point2;
			maxPoint = point1;
		}
		minAngle = p.angleTo(minPoint);
		maxAngle = p.angleTo(maxPoint);
	}
	
	public static void updateWallsTo(Point p) {
		for(Wall wall: walls) {
			wall.updateToPoint(p);
		}
	}
	public float getMinAngle() {
		return minAngle;
	}
	public float getMaxAngle() {
		return maxAngle;
	}
	public Point getMinPoint() {
		return minPoint;
	}
	public Point getMaxPoint() {
		return maxPoint;
	}

	public Point getPoint1() {
		return point1;
	}
	public Point getPoint2() {
		return point2;
	}

	public Point getArcPosWith(Point oldPos) {
		Point perpPoint = this.getPerpPoint(oldPos);
		float newX = 2 * perpPoint.getX() - oldPos.getX();
		float newY = 2 * perpPoint.getY() - oldPos.getY();
		return new Point(newX, newY);
	}
}
