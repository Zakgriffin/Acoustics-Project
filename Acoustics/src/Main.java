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
		
		wave = new Arc(0.3f, 3 * PI / 2 - 0.4f, null);
		wave.setPos(new Point(250, 150));
		
		//new Wall(150, 400, 340, 350);
	}
	
	float time = 0;
	float offset = 1f;
	public void draw() {
		background(0);
		strokeWeight(2);
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
			/*
			if(!arc.getType().equals("passing")) {
				
				ArrayList<Arc> secArcs = arc.generateArcs("");
				
				for(Arc secArc: secArcs) {
					//secArc.show(time);
					arc(secArc.getPos().x, secArc.getPos().y, 60, 60, secArc.getMinLim(), secArc.getMaxLim());
				}
				
				// v
				Wall wall = arc.getWall();
				Point p = Geo.boost(arc.getPos(), wall.getPerpPoint(arc.getPos()));
				float min = Geo.angleBoost(arc.getMaxLim(), wall.getAngle());
				float max = Geo.angleBoost(arc.getMinLim(), wall.getAngle());
				if(max < min) {
					max += PI * 2;
				}
				System.out.println("Min: " +  Math.toDegrees(min) + "  Max: " + Math.toDegrees(max));
				
				arc(p.x, p.y, 60, 60, min, max);
				// ^
				
			}
			*/
			
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
}