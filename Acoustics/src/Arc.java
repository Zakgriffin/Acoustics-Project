import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class Arc {
	public static ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	private static PApplet p;
	private Point pos;
	private float r = 30;
	
	private float minAngLim;
	private float maxAngLim;
	
	float perpDist;
	
	private Wall wall;
	
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point source) {
		this.minAngLim = minAngLim;
		this.maxAngLim = maxAngLim;
		this.wall = wall;
		this.pos = source;
		if(wall != null) {
			perpDist = pos.distanceTo(wall.getPerpPoint(pos));
		} else {
			perpDist = 100;
		}
		//arcs.add(this);
	}
	public Arc(float minAngLim, float maxAngLim) {
		this(minAngLim, maxAngLim, null, null);
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	public void update() {
		r += 2;
	}
	public void show() {
		float x = pos.getX();
		float y = pos.getY();
		
		p.pushMatrix();
			p.fill(0, 0, 255, 100);
			p.arc(x, y, r, r, maxAngLim - 2 * (float) Math.PI, minAngLim);
		p.popMatrix();
	}
	public Point getPos() {
		return pos;
	}
	public void setPos(Point pos) {
		this.pos = pos;
	}
	public void setPos(float x, float y) {
		this.pos.setX(x);
		this.pos.setY(y);
	}

	public ArrayList<Arc> generateArcs2() {
		ArrayList<AngVect> angs = new ArrayList<AngVect>();
		ArrayList<AngVect> started = new ArrayList<AngVect>();
		
		for(Wall wall: Wall.walls) {
			Point p1 = wall.getPoint1();
			Point p2 = wall.getPoint2();
			
			Point pMax;
			Point pMin;
			float angleToMin;
			float angleToMax;
			
			// determine which is max and min
			float angleTo1 = pos.angleTo(p1);
			float angleTo2 = pos.angleTo(p2);
			//                     haha, I used an XOR v
			if(Math.abs(angleTo1 - angleTo2) > Math.PI ^ angleTo1 - angleTo2 < 0) {
				pMin = p1;
				pMax = p2;
				angleToMin = angleTo1;
				angleToMax = angleTo2;
			} else {
				pMin = p2;
				pMax = p1;
				angleToMin = angleTo2;
				angleToMax = angleTo1;
			}
			
			angleToMin = pos.angleTo(pMin);
			angleToMax = pos.angleTo(pMax);
			AngVect minAngVect = new AngVect(pMin, angleToMin, wall, true);
			AngVect maxAngVect = new AngVect(pMax, angleToMax, wall, false);
			minAngVect.setLink(maxAngVect);
			maxAngVect.setLink(minAngVect);
			
			if(angleFallsIn(angleToMin, minAngLim, maxAngLim)) {
				angs.add(minAngVect);
			}
			
			if(angleFallsIn(angleToMax, minAngLim, maxAngLim)) {
				angs.add(maxAngVect);
			}
			
			//left side of of wall is before start and right side is after
			if(angleFallsIn(minAngLim, angleToMin, angleToMax)) {
				started.add(maxAngVect);
			}
			//p.ellipse(minAngVect.pos.x, minAngVect.pos.y, 5, 5);
		}
		ArrayList<Arc> subArcs = new ArrayList<Arc>();
		if(angs.size() == 0) {
			if(started.size() > 0) {
				AngVect best = findBest(started, minAngLim);
				subArcs.add(new Arc(minAngLim, maxAngLim, best.wall, this.pos));
			} else {
				subArcs.add(new Arc(minAngLim, maxAngLim, null, this.pos));
			}
			return subArcs;
		}
		Collections.sort(angs);
		// rotate all AngVects so starts with right after min and ends before max
		Collections.rotate(angs, getRotatePoint(angs));
		
		//add first arc touching min lim
		AngVect first = angs.get(0);
		AngVect lastAngVect;
		float lastAngle;
		int i;
		if(started.size() > 0) {
			AngVect best = findBest(started, minAngLim);
			lastAngVect = best.linkAngVect;
			lastAngle = minAngLim;
			i = 0;
		} else {
			// generate new arc and start at angs[0]
			subArcs.add(new Arc(minAngLim, first.angle, null, this.pos));
			lastAngle = first.angle;
			lastAngVect = first;
			i = 1;
		}
		for(; i < angs.size(); i++) {
			AngVect ang = angs.get(i); // <-- possible arc edge
			if(ang.isMin) {
				// began new wall
				started.add(ang.linkAngVect);
			} else {
				// hit end of some wall
				started.remove(ang);
			}
			
			if(ang == lastAngVect.linkAngVect) {
				// stop searching for interrupt points, got to end
				AngVect best = findBest(started, ang.pos);
				
				// add arc before fall
				subArcs.add(new Arc(lastAngle, ang.angle, ang.wall, this.pos));
				lastAngle = ang.angle;
				
				if(best != null) {
					// found winning candidate wall
					lastAngVect = best.linkAngVect;
					//i++;
				} else {
					// never found any candidate walls
					// ADD STUFF FOR NON REFLECTIONS
					if(i + 1 >= angs.size()) {
						break;
						// already at end loop, end all
					}
					// create the passing arc in addition to the first
					AngVect nextAngVect = angs.get(i + 1);
					Arc passingArc = new Arc(lastAngle, nextAngVect.angle, null, this.pos);
					
					subArcs.add(passingArc);
					
					lastAngVect = nextAngVect;
					lastAngle = nextAngVect.angle;
					// increase index again since two arcs made
					i++;
				}
				// end of drop wall and passing arc type
			} else if(ang.isMin && !intersects(lastAngVect.pos, lastAngVect.linkAngVect.pos, pos, ang.pos)) {
				subArcs.add(new Arc(lastAngle, ang.angle, lastAngVect.wall, this.pos));
				lastAngle = ang.angle;
				
				lastAngVect = ang;
				// end of interrupt wall type
			}
		}
		if(angleFallsIn(lastAngVect.linkAngVect.angle, minAngLim, maxAngLim)) {
			subArcs.add(new Arc(lastAngle, maxAngLim, null, this.pos));
		} else {
			subArcs.add(new Arc(lastAngle, maxAngLim, lastAngVect.wall, this.pos));
		}
		System.out.println("Count: " + subArcs.size());
		//System.out.println("Final: " + subArcs.toString());
		return subArcs;
	}
	
	private int getRotatePoint(ArrayList<AngVect> list) {
		for(int i = list.size() - 1; i > 0; i--) {
			if(list.get(i).angle < maxAngLim) {
				return i + 1;
			}
		}
		return 0;
	}
	private boolean intersects(Point p1, Point q1, Point p2, Point q2) {
		int o1 = orientation(p1, q1, p2); 
	    int o2 = orientation(p1, q1, q2); 
	    int o3 = orientation(p2, q2, p1); 
	    int o4 = orientation(p2, q2, q1);
	    
	    if (o1 != o2 && o3 != o4) {
	    	return true;
	    }
	    return false;
	    // doesn't cover collinear pairs
	}
	private int orientation(Point p, Point q, Point r) {
		float px = p.getX();
		float py = p.getY();
		float qx = q.getX();
		float qy = q.getY();
		float rx = r.getX();
		float ry = r.getY();
	    float val = (qy - py) * (rx - qx) - (qx - px) * (ry - qy); 
	    
	    if(val == 0) {
	    	return 0; //collinear
	    } else if(val > 0) {
	    	return 1; //CW
	    } else {
	    	return -1; //CCW
	    }
	}
	
	public static boolean angleFallsIn(float testAngle, float min, float max) {
		if(min > max) {
			min -= 2 * Math.PI;
		}
		if(testAngle < min) {
			testAngle += 2 * Math.PI;
		}
		if(testAngle > min && testAngle < max) {
			return true;
		}
		return false;
	}
	private float judgeDist(Point dropPoint, Point a, Point b) {
		// slope of ray
		float m1 = (pos.getY() - dropPoint.getY()) / (pos.getX() - dropPoint.getX());
		
		// slope of line segment
		float m2 = (a.getY() -b.getY()) / (a.getX() - b.getX());
		
		float x1 = pos.getX();
		float y1 = pos.getY();
		
		// x and y of intersection point
		float interX = (m1 * x1 - m2 * a.getX() - y1 + a.getY()) / (m1 - m2);
		float interY = m1 * (interX - x1) + y1; // mhm add y1...
		
		float deltaX = interX - x1;
		float deltaY = interY - y1; // and... subtract it? kden
		
		// final distance between pos and intersection point without sqrt for speed
		return deltaX * deltaX +  deltaY *  deltaY;
	}
	
	private AngVect findBest(ArrayList<AngVect> started, Point fallPoint) {
		AngVect best = null;
		float bestDist = -1;
		
		// cycle through "started" containing all walls that have started
		for(AngVect newBest: started) {
			float newBestDist = judgeDist(fallPoint, newBest.pos, newBest.linkAngVect.pos);
			if(newBestDist < bestDist || bestDist == -1) {
				best = newBest;
				bestDist = newBestDist;
			}
		}
		return best;
	}
	private AngVect findBest(ArrayList<AngVect> started, float rayAngle) {
		float x = (float) Math.cos(rayAngle);
		float y = (float) Math.sin(rayAngle);
		return findBest(started, new Point(x + pos.getX(), y + pos.getY()));
		
	}
	
	public float getMinLim() {
		return minAngLim;
	}
	public float getMaxLim() {
		return maxAngLim;
	}
	
	public String toString() {
		return "(" + minAngLim + " -> " + maxAngLim + ")";
	}
	
	public Wall getWall() {
		return wall;
	}
}
