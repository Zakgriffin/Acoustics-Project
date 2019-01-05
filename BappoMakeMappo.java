import java.util.ArrayList;

import processing.core.PApplet;

public class BappoMakeMappo {
	public static ArrayList<cube> cubes = new ArrayList<cube>();
	
	private static PApplet p;
	
	private int x;
	private int y;
	private int c;
	private int numX;
	private int numY;
	private int lastX;
	private int lastY;
	
	
	public BappoMakeMappo(int X, int Y, int C) {
		x=X;
		y=Y;
		c=C;
	}
	
	public BappoMakeMappo(int X, int Y) {
		this(X, Y, 5);
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	
	public ArrayList<cube> makeMap(){
		
		//Number of cubes that should be made for x and y
		numX = x/c;
		numY = y/c;
		
		//Exception cube Stuff
		if(x%c!=0){
			lastX = c-(x%c);
		}else {
			lastX = c;
		}
		if(y%c!=0) {
			lastY = c-(y%c);
		}else {
			lastY = c;
		}
		
		//Create regularly sized cubes
		for(int i = 0;i<=numY;i++) {
			for(int k = 0;k<=numX;k++) {
			    if(k==numX&&i==numY) {
			    	//This is the bottom right corner
					cube blah = new cube(i*c,k*c,lastX,lastY);
					cubes.add(blah);
				}else if(k==numX) {
					//This is the last Vertical row
					cube blah = new cube(i*c,k*c,lastX,c);
					cubes.add(blah);					
				}else if(i==numY){
					//This is the last Horizontal row
					cube blah = new cube(i*c,k*c,c,lastY);
					cubes.add(blah);
				}else {
					cube blah = new cube(i*c,k*c,c);
					cubes.add(blah);
				}
			}
		}
		
		for(int i = 0;i<=numX;i++) {
			cube blah = new cube(i*c,y,c,lastY);
			cubes.add(blah);
		}
		
		for(cube i:cubes) {
			p.rect(i.getX(),i.getY(),i.getSizeX(),i.getSizeY());
		}
		return cubes;
	}
	
	
	
}
