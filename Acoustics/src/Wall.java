import java.util.ArrayList;

import processing.core.PApplet;

public class Wall {
	
	// Walls are objects that arcs can bounce off of. They are seen as line segments
	
	public static ArrayList<Wall> walls = new ArrayList<Wall>();
	
	private static PApplet p;
	private Point point1;
	private Point point2;
	private Point[] points;
	private float angle;
	
	int[] color;
	
	public Wall(float x1, float y1, float x2, float y2) {
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
		points = new Point[] {point1, point2};
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
	Point held;
	public void update() {
		Point center;
		Point[] movePoints = new Point[3];
		if(held != null && held != point1 && held != point2) {
			// translating
			center = held;
		} else {
			center = Geo.center(points[0], points[1]);
		}
		movePoints[0] = point1;
		movePoints[1] = point2;
		movePoints[2] = center;
		for(Point point: movePoints) {
			Point mouse = new Point(p.mouseX, p.mouseY);
			if(point.distanceTo(mouse) < 8 || held == point) {
				p.pushStyle();
					if(p.mousePressed) {
						held = point;
						p.stroke(255 - color[0], 255 - color[1], 255 - color[2]);
						if(point == movePoints[2]) {
							// translate wall
							float xDif = point.x - point1.x;
							float yDif = point.y - point1.y;
							point.set(mouse);
							point1.set(mouse);
							point2.set(mouse);
							point1.add(xDif, yDif);
							point2.add(-xDif, -yDif);
						} else {
							// move vert
							point.set(mouse);
						}
					} else {
						held = null;
					}
					p.fill(color[0], color[1], color[2]);
					p.ellipse(point.x, point.y, 10, 10);
				p.popStyle();
				
				if(key == 'x') {
					walls.set(walls.indexOf(this), null);
				} else if(key == 'f') {
					System.out.println("Wall ID: " + walls.indexOf(this));
				}
			}
		}
	}
	public static void updateWalls() {
		for(Wall wall: walls) {
			wall.update();
		}
		walls.remove(null);
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
		return points;
	}
	public float getAngle() {
		return angle;
	}
	static char key;
	static boolean qHeld = false;
	public static void keyPressed(char key) {
		Wall.key = key;
		if(key == 'q') {
			if(!qHeld) {
				float x = p.mouseX;
				float y = p.mouseY;
				new Wall(x + 20, y, x - 20, y);
				qHeld = true;
			}
		} else {
			qHeld = false;
		}
	}
}
