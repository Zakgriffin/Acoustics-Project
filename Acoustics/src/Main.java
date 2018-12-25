import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
		
		wave = new Arc(0.3f, 3 * PI / 2 - 0.4f);
		wave.setPos(new Point(250, 150));
		
		//new Wall(150, 400, 340, 350);
	}
	
	float mx = 0;
	float my = 0;
	float r;
	
	public void draw() {
		if(mouseX != mx || mouseY != my) {
			background(200);
			Wall.showWalls();
			//Wall.updateWallsTo(wave.getPos());
			
			wave.setPos(mouseX, mouseY);
			wave.show();
			ArrayList<Arc> arcs = wave.generateArcs2();

			pushStyle();
				for(Arc arc: arcs) {
					if(arc != null) {
						if(arc.getWall() != null) {
							int[] a = arc.getWall().color;
							fill(a[0], a[1], a[2], 60);
							stroke(a[0], a[1], a[2]);
						} else {
							noFill();
							stroke(0);
						}
						arc(wave.getPos().getX(), wave.getPos().getY(), arc.perpDist * 2, arc.perpDist * 2, arc.getMinLim(), arc.getMaxLim(), PIE);
					}
				}
			popStyle();
			System.out.println("FPS: " + frameRate);
			//noLoop();
		}
		mx = mouseX;
		my = mouseY;
		
		
		/*
		background(0, 0, 0);
		pushStyle();
		float min = 0;
		float max = PI / 2;
		float x = 140;
		float y = 70;
		float a = 10;
		float f = 80;
		int g = 3;
		r += 10;
		float j = 30 / g;
		blendMode(ADD);
		background(128, 0, 128);
		fill(f/ 4, 0, f/ 4);
		for(int i = 0; i < g * 4; i++) {
			if(i == g) {
				blendMode(SUBTRACT);
			} else if(i == g * 3) {
				blendMode(ADD);
			}
			arc(x, y, r - i * j, r - i * j, min, max);
		}
		for(int i = 0; i < g * 4; i++) {
			if(i == g) {
				blendMode(SUBTRACT);
			} else if(i == g * 3) {
				blendMode(ADD);
			}
			arc(mouseX, mouseY, r - i * j, r - i * j, min - 0.8f, max - 0.6f);
		}
			
			blendMode(DIFFERENCE);
			fill(0, 0, 255);
			rect(0, 0, width, height);
			
			blendMode(SUBTRACT);
			fill(128, 0, 128);
			rect(0, 0, width, height);
			
		popStyle();
		*/
		
		/*
		pushStyle();
			background(0);
			noFill();
			stroke(255);
			strokeCap(SQUARE);
			float min = 0;
			float max = PI / 2;
			float x = 140;
			float y = 70;
			int waveLength = 60;
			int amp = 255;
			int density = 2;
			r = 130;
			float dist = (float) waveLength / (density * 4 + 1);
			strokeWeight(dist);
			
			stroke(255, 0, 0);
			line(x, y - 5, x + r, y - 5);
			stroke(255);
			line(x, y - 5, x + r - waveLength, y - 5);
			
			float b = r * 2 - dist;
			for(int i = 2; i <= density * 2; i += 2) {
				System.out.println("test");
				stroke(20 * i);
				arc(x, y, b - dist * i, b - dist * i, 0, HALF_PI);
			}

			
			//add
			stroke(30);
			arc(x, y, b - dist * 2, b - dist * 2, 0, HALF_PI);
			stroke(60);
			arc(x, y, b - dist * 4, b - dist * 4, 0, HALF_PI);
			
			stroke(30);
			arc(x, y, b - dist * 6, b - dist * 6, 0, HALF_PI);
			
			//sub
			stroke(30);
			arc(x, y, b - dist * 10, b - dist * 10, 0, HALF_PI);
			stroke(60);
			arc(x, y, b - dist * 12, b - dist * 12, 0, HALF_PI);
			stroke(30);
			arc(x, y, b - dist * 14, b - dist * 14, 0, HALF_PI);
			stroke(0, 255, 0);
			
		popStyle();
		*/
	}
}