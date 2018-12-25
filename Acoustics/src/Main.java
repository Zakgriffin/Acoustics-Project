import java.util.ArrayList;

import processing.core.PApplet;

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
		
		wave = new Arc(0.3f, 3 * PI / 2 - 0.4f, null, null);
		wave.setPos(new Point(250, 150));
		
		//new Wall(150, 400, 340, 350);
	}
	
	float mx = 0;
	float my = 0;
	float r;
	float time = 0;
	
	public void draw() {
		background(200);
		time += 0.3;
		Wall.showWalls();
		
		wave.setPos(mouseX, mouseY);
		ArrayList<Arc> arcs = wave.generateArcs2();

		for(Arc arc: arcs) {
			arc.show(time);
		}
		//System.out.println("FPS: " + frameRate);
	}
	
	public void mouseClicked() {
		time = 0;
	}
}