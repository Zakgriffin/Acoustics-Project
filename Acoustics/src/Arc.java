import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;
import processing.core.PConstants;

public class Arc {
	public static ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	private static PApplet p;
	private Point pos;
	private Point rebound;
	private String type;
	
	private float minAngLim;
	private float maxAngLim;
	
	float perpDist;
	
	private Wall wall;
	
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point source, String type) {
		this.minAngLim = minAngLim;
		this.maxAngLim = maxAngLim;
		this.wall = wall;
		this.pos = source;
		this.type = type;
		if(wall != null) {
			perpDist = pos.distanceTo(wall.getPerpPoint(pos));
		} else {
			perpDist = 100;
		}
		if(type.equals("passing")) {
			this.rebound = null;
		} else if(wall != null) {
			this.rebound = Geo.boost(pos, wall.getPerpPoint(source));
		}
		//arcs.add(this);
	}
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point source) {
		this(minAngLim, maxAngLim, wall, source, "");
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	public void update() {
		//r += 2;
	}
	public void show(float time) {
		if(wall != null) {
			int[] a = wall.color;
			//p.fill(a[0], a[1], a[2], 60);
			p.noFill();
			p.stroke(a[0], a[1], a[2]);
			if(rebound != null) {
				if(time > perpDist) {
					Point r = rebound;
					float ang = wall.getAngle();
					float min = Geo.angleBoost(maxAngLim, ang);
					float max = Geo.angleBoost(minAngLim, ang);
					if(min > max) {
						max += 2 * Math.PI;
					}
					p.arc(r.x, r.y, time * 2, time * 2, min, max, PConstants.PIE);
					p.ellipse(r.x, r.y, 5, 5);
				} else {
					p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, maxAngLim, PConstants.PIE);
				}
			}
			Point[] inters = Geo.segCircIntersects(wall.getPoint1(), wall.getPoint2(), pos, time);
			if(inters != null) {
				for(Point inter: inters) {
					p.ellipse(inter.x, inter.y, 5, 5);
				}
			}
		} else {
			p.noFill();
			p.stroke(0);
			p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, maxAngLim, PConstants.PIE);
		}
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
			
			if(Geo.angleFallsIn(angleToMin, minAngLim, maxAngLim)) {
				angs.add(minAngVect);
			}
			
			if(Geo.angleFallsIn(angleToMax, minAngLim, maxAngLim)) {
				angs.add(maxAngVect);
			}
			
			//left side of of wall is before start and right side is after
			if(Geo.angleFallsIn(minAngLim, angleToMin, angleToMax)) {
				started.add(maxAngVect);
			}
			//p.ellipse(minAngVect.pos.x, minAngVect.pos.y, 5, 5);
		}
		ArrayList<Arc> subArcs = new ArrayList<Arc>();
		if(angs.size() == 0) {
			if(started.size() > 0) {
				AngVect best = findBest(started, minAngLim);
				subArcs.add(new Arc(minAngLim, maxAngLim, best.wall, pos));
			} else {
				subArcs.add(new Arc(minAngLim, maxAngLim, null, pos));
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
			subArcs.add(new Arc(minAngLim, first.angle, null, pos));
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
				subArcs.add(new Arc(lastAngle, ang.angle, ang.wall, pos));
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
					Arc passingArc = new Arc(lastAngle, nextAngVect.angle, null, pos);
					
					subArcs.add(passingArc);
					
					lastAngVect = nextAngVect;
					lastAngle = nextAngVect.angle;
					// increase index again since two arcs made
					i++;
				}
				// end of drop wall and passing arc type
			} else if(ang.isMin && !Geo.intersects(lastAngVect.pos, lastAngVect.linkAngVect.pos, pos, ang.pos)) {
				subArcs.add(new Arc(lastAngle, ang.angle, lastAngVect.wall, pos));
				lastAngle = ang.angle;
				
				lastAngVect = ang;
				// end of interrupt wall type
			}
		}
		if(Geo.angleFallsIn(lastAngVect.linkAngVect.angle, minAngLim, maxAngLim)) {
			subArcs.add(new Arc(lastAngle, maxAngLim, null, this.pos));
		} else {
			subArcs.add(new Arc(lastAngle, maxAngLim, lastAngVect.wall, pos));
		}
		System.out.println("Count: " + subArcs.size());
		//System.out.println("Final: " + subArcs.toString());
		return subArcs;
	}
	
	private int getRotatePoint(ArrayList<AngVect> list) {
		// Returns an index to rotate list by
		for(int i = list.size() - 1; i > 0; i--) {
			if(list.get(i).angle < maxAngLim) {
				return i + 1;
			}
		}
		return 0;
	}

	private AngVect findBest(ArrayList<AngVect> started, Point dropPoint) {
		AngVect best = null;
		float bestDist = -1;
		
		// cycle through "started" containing all walls that have started
		for(AngVect newBest: started) {
			//float newBestDist = judgeDist(fallPoint, newBest.pos, newBest.linkAngVect.pos);
			float newBestDist = Geo.raySegDist(pos, dropPoint, newBest.pos, newBest.linkAngVect.pos, true);
			if(newBestDist < bestDist || bestDist == -1) {
				best = newBest;
				bestDist = newBestDist;
			}
		}
		return best;
	}
	private AngVect findBest(ArrayList<AngVect> started, float rayAngle) {
		// used for special cases where only have angle, not point. Avoid since trig used
		float x = (float) Math.cos(rayAngle);
		float y = (float) Math.sin(rayAngle);
		return findBest(started, new Point(x + pos.x, y + pos.y));
		
	}
	
	
	public Point getPos() {
		return pos;
	}
	public void setPos(Point pos) {
		this.pos = pos;
	}
	public void setPos(float x, float y) {
		this.pos.x = x;
		this.pos.y = y;
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
	public String getType() {
		return type;
	}
	public Point getRebound() {
		return rebound;
	}
}
