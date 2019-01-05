import java.util.ArrayList;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class Main extends PApplet {
	Arc wave;
	
	//Mappo and Screeno
	public int sizX = 500;
	public int sizY = 500;
	public int sizC = 3;
	
	public static void main(String[] args) {
		PApplet.main("Main");
	}
	
	public void settings() {
		// Processing canvas initialization
		size(sizX, sizY);
		pixelDensity(2);
	}
	
	public void setup() {
		// Called once on startup
		startup();
		//startupDebug();
	}
	
	public void draw() {
		// Called periodically around 60 times a second
		periodic();
		//periodicDebug();
	}
	
	private void startup() {
		Wall.setP(this);
		Arc.setP(this);
		BappoMakeMappo.setP(this);
		
		//Mappo
		BappoMakeMappo k = new BappoMakeMappo(sizX,sizY,sizC);
		ArrayList<cube> testo = k.makeMap();
		/*
		//Test the arraylist of cubes
		for(cube i:testo) {
			System.out.println(i);
		}
		*/
		
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
	float increment = 5f;
	
	private void periodic() {
		background(0);
		strokeWeight(2);
		if(keyPressed) {
			Wall.keyPressed(key);
		} else {
			Wall.keyPressed('-');
		}
		if(keyPressed) {
			if (key == 'w') {
				time += increment;
			} else if(key == 's') {
				time -= increment;
			}
			if(time < 0) {
				time = 0;
			}
		}
		Wall.showWalls();
		Wall.updateWalls();
		
		wave.setPos(mouseX, mouseY);
		pushStyle();
			fill(255, 150);
			stroke(255);
			arc(wave.getPos().x, wave.getPos().y, 30, 30, wave.getMaxLim() - TWO_PI, wave.getMinLim());
		popStyle();
		
		ArrayList<Arc> arcs = wave.generateArcs();
		recurseArcs(arcs, time); // <-- big boi method here
		System.out.println("Number of arcs: " + arcCount);
		arcCount = 0;
		/*
		for(Arc arc: arcs) {
			arc.show(time);
			if(!arc.getType().equals("passing")) {
				ArrayList<Arc> arcs2 = arc.mirrorArc().generateArcs();
				for(Arc arc2: arcs2) {
					arc2.show(time);
				}
			}
		}
		*/
		//System.out.println("FPS: " + frameRate);
	}
	
	private static int arcCount = 0;
	private static void recurseArcs(ArrayList<Arc> arcs, float time) {
		for(Arc arc: arcs) {
			arcCount++;
			arc.show(time);
			if(arc.hasTouched(time)) {
				// arc has touched its wall, break into sub arcs
				ArrayList<Arc> subArcs = arc.mirrorArc().generateArcs();
				// call self with new subarcs
				recurseArcs(subArcs, time);
			}
		}
	}
	
	public void mouseClicked() {
		// Used to quickly reset time
		time = 0;
	}
	public void mouseWheel(MouseEvent event) {
		// Increases or decreases time based on scroll
		float e = event.getCount();
		time -= e;
		if(time < 0) {
			time = 0;
		}
	}
	
	Point c;
	Wall min;
	Wall max;
	private void startupDebug() {
		Wall.setP(this);
		Arc.setP(this);
		
		c = new Point(width / 2, height / 2);
		
		min = new Wall(c.x, c.y, c.x + 40, c.y);
		max = new Wall(c.x, c.y, c.x, c.y + 40);
	}
	
	private void periodicDebug() {
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