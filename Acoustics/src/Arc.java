import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;
import processing.core.PConstants;

public class Arc {
	
	// Arcs are the visible shapes caused by pressure waves
	
	public static ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	private static PApplet p;
	private Point pos;
	private Point rebound;
	private String type;
	
	private float minAngLim;
	private float maxAngLim;
	
	float perpDist;
	
	private Wall wall;
	
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point pos, String type) {
		this.minAngLim = minAngLim;
		this.maxAngLim = maxAngLim;
		this.wall = wall;
		this.pos = pos;
		this.type = type;
		if(type.equals("passing")) {
			this.rebound = null;
		} else {
			this.rebound = Geo.boost(pos, wall.getPerpPoint(pos));
		}
		//arcs.add(this);
	}
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point pos) {
		this(minAngLim, maxAngLim, wall, pos, "");
	}
	
	public static void setP(PApplet pa) {
		p = pa;
	}
	
	public void show(float time) {
		p.pushStyle();
		if(!type.equals("passing")) {
			// reflective arc
			
			int[] a = wall.color;
			p.fill(a[0], a[1], a[2], 60);
			//p.noFill();
			p.stroke(a[0], a[1], a[2]);
			
			// get the intersection points between walls and arcs
			Point[] inters = Geo.segCircIntersects(wall.getPoint1(), wall.getPoint2(), pos, time);
			
			boolean simpleArc = true;
			if(inters != null) {
				// points on wall visible to pos
				Point[] visPoints = new Point[2];
				visPoints[0] = Geo.raySegIntersect(pos, minAngLim, wall.getPoints());
				visPoints[1] = Geo.raySegIntersect(pos, maxAngLim, wall.getPoints());
				if(Geo.rectIntersect(visPoints, inters)) {
					simpleArc = false;
				}
			}
			
			if(simpleArc) {
				// arc has no interaction with wall and can be rendered simply
				p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, maxAngLim);
			} else {
				// arc is split into 3 pieces: 2 small arcs, 1 triangle
				AngVect[] limits = orderMinMax(inters, pos);
				Point pMin = limits[0].pos;
				Point pMax = limits[1].pos;
				float minAng = limits[0].angle;
				float maxAng = limits[1].angle;
				
				if(Geo.angleFallsIn(minAng, minAngLim, maxAngLim)) {
					// ensure intersection point is touching wall
					p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, minAng);
				} else {
					// if not, don't draw arc part and make triangle use wall end point
					pMin = Geo.raySegIntersect(pos, minAngLim, wall.getPoint1(), wall.getPoint2());
				}
				// repeat ^
				if(Geo.angleFallsIn(maxAng, minAngLim, maxAngLim)) {
					p.arc(pos.x, pos.y, time * 2, time * 2, maxAng, maxAngLim);
				} else {
					pMax = Geo.raySegIntersect(pos, maxAngLim, wall.getPoint1(), wall.getPoint2());
				}
				
				p.pushStyle();
					p.noStroke();
					p.triangle(pMin.x, pMin.y, pMax.x, pMax.y, pos.x, pos.y);
				p.popStyle();
			}
		} else {
			// passing arc
			p.noFill();
			p.stroke(255);
			p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, maxAngLim);
		}
		p.popStyle();
		
		
		/*
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
				
			}
			p.arc(pos.x, pos.y, time * 2, time * 2, minAngLim, maxAngLim, PConstants.PIE);
		}
		*/
	}
	
	private AngVect[] orderMinMax(Point[] unorg, Point view) {
		Point pMin = unorg[0];
		Point pMax = unorg[1];
		float minAng = pos.angleTo(pMin);
		float maxAng = pos.angleTo(pMax);
		
		if(Geo.firstIsMin(maxAng, minAng)) {
			// flip points and angles if reversed
			float tempA = minAng;
			minAng = maxAng;
			maxAng = tempA;
			Point tempP = pMin;
			pMin = pMax;
			pMax = tempP;
		}
		
		AngVect min = new AngVect(pMin, minAng, true);
		AngVect max = new AngVect(pMax, maxAng, false);
		return new AngVect[] {min, max};
	}
	public ArrayList<Arc> generateArcs(String mode) {
		Point oldPos = pos;
		float oldMinAngLim = minAngLim;
		float oldMaxAngLim = maxAngLim;
		
		boolean tempSource = !mode.equals("source") && !type.equals("passing");
		
		if(tempSource) {
			// TEMP
			pos = Geo.boost(pos, wall.getPerpPoint(pos));
			minAngLim = Geo.angleBoost(maxAngLim, wall.getAngle());
			maxAngLim = Geo.angleBoost(minAngLim, wall.getAngle());
			if(maxAngLim < minAngLim) {
				maxAngLim += Math.PI * 2;
			}
		}
		
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
			if(Geo.firstIsMin(angleTo1, angleTo2)) {
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
			// only single arc
			if(started.size() > 0) {
				// reflective arc
				AngVect best = findBest(started, minAngLim);
				subArcs.add(new Arc(minAngLim, maxAngLim, best.wall, pos));
			} else {
				// passing arc
				subArcs.add(new Arc(minAngLim, maxAngLim, null, pos, "passing"));
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
		int startAt;
		if(started.size() > 0) {
			// generate new reflective arc and start at angs[0]
			AngVect best = findBest(started, minAngLim);
			lastAngVect = best.linkAngVect;
			lastAngle = minAngLim;
			startAt = 0;
		} else {
			// generate new passing arc and start at angs[1]
			subArcs.add(new Arc(minAngLim, first.angle, null, pos, "passing"));
			lastAngle = first.angle;
			lastAngVect = first;
			startAt = 1;
		}
		
		for(int i = startAt; i < angs.size(); i++) {
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
				} else {
					// never found any candidate walls
					if(i + 1 >= angs.size()) {
						break;
						// already at end loop, end all
					}
					// create the passing arc in addition to the first
					AngVect nextAngVect = angs.get(i + 1);
					Arc passingArc = new Arc(lastAngle, nextAngVect.angle, null, pos, "passing");
					
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
			subArcs.add(new Arc(lastAngle, maxAngLim, null, this.pos, "passing"));
		} else {
			subArcs.add(new Arc(lastAngle, maxAngLim, lastAngVect.wall, pos));
		}
		//System.out.println("Count: " + subArcs.size());
		//System.out.println("Final: " + subArcs.toString());
		
		if(tempSource) {
			// TEMP
			pos = oldPos;
			minAngLim = oldMinAngLim;
			maxAngLim = oldMaxAngLim;
		}
		
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
