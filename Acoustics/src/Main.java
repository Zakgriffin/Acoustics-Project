import java.util.ArrayList;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class Main extends PApplet {
	Arc wave;
	Wall wall;
	public static void main(String[] args) {
		PApplet.main("Main");
	}
	
	public void settings() {
		size(500, 500);
		pixelDensity(2);
	}
	
	public void setup() {
		setupFunc();
		//setupDebug();
	}
	
	public void draw() {
		drawFunc();
		//drawDebug();
	}
	
	public void setupFunc() {
		Wall.setP(this);
		Arc.setP(this);
		
		new Wall(200, 100, 270, 100);
		new Wall(160, 130, 190, 200);
		
		new Wall(350, 130, 300, 200);
		new Wall(200, 300, 250, 300);
		new Wall(120, 230, 145, 190);
		new Wall(240, 250, 320, 230);
		new Wall(300, 320, 300, 250);
		
		
		/*
		new Wall(170, 200, 220, 200);
		new Wall(270, 250, 320, 250);
		new Wall(150, 225, 340, 225);
		*/
		
		wave = new Arc(0.3f, 3 * PI / 2 - 0.4f, null);//0.3f, 3 * PI / 2 - 0.4f
		wave.setPos(new Point(250, 150));
		
		//new Wall(150, 400, 340, 350);
	}
	
	float time = 0;
	float offset = 1f;
	
	public void drawFunc() {
		background(0);
		//strokeWeight(2);
		if(keyPressed) {
			Wall.keyPressed(key);
		} else {
			Wall.keyPressed('-');
		}
		if(keyPressed) {
			if (key == 'w') {
				time += offset;
			} else if(key == 's') {
				time -= offset;
			}
			if(time < 0) {
				time = 0;
			}
		}
		Wall.showWalls();
		Wall.updateWalls();
		
		wave.setPos(mouseX, mouseY);
		ArrayList<Arc> arcs = wave.generateArcs("source");
		pushStyle();
			fill(255, 150);
			stroke(255);
			arc(wave.getPos().x, wave.getPos().y, 30, 30, wave.getMaxLim() - TWO_PI, wave.getMinLim());
			
		popStyle();
		for(Arc arc: arcs) {
			arc.show(time);
			if(!arc.getType().equals("passing")) {
				ArrayList<Arc> arcs2 = arc.mirrorArc().generateArcs("");
				for(Arc arc2: arcs2) {
					arc2.show(time);
				}
			}
		}
		//System.out.println("FPS: " + frameRate);
	}
	
	public void mouseClicked() {
		time = 0;
	}
	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();
		time -= e;
		if(time < 0) {
			time = 0;
		}
	}
	
	Point c;
	Wall min;
	Wall max;
	public void setupDebug() {
		Wall.setP(this);
		Arc.setP(this);
		
		c = new Point(width / 2, height / 2);
		
		min = new Wall(c.x, c.y, c.x + 40, c.y);
		max = new Wall(c.x, c.y, c.x, c.y + 40);
	}
	
	public void drawDebug() {
		background(0);
		
		if(keyPressed) {
			Wall.keyPressed(key);
		} else {
			Wall.keyPressed('-');
		}
		Wall.showWalls();
		Wall.updateWalls();
		
		Point mouse = new Point(mouseX, mouseY);
		
		stroke(255);
		line(c.x, c.y, mouse.x, mouse.y);
		
		System.out.println("MinAng: " + Math.toDegrees(min.getAngle()));
		System.out.println("MaxAng: " + Math.toDegrees(max.getAngle()));
		System.out.println(Geo.angleFallsIn(c.angleTo(mouse), min.getAngle(), max.getAngle()));
	}
}