import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;
import processing.core.PConstants;

public class Arc {
	
	// Arcs are used to visualize pressure waves
	
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
	
	public Arc(float minAngLim, float maxAngLim, Point pos) {
		this(minAngLim, maxAngLim, null, pos, "passing");
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
				visPoints[0] = Geo.raySegIntersect(pos, minAngLim, wall.getPoints(), true);
				visPoints[1] = Geo.raySegIntersect(pos, maxAngLim, wall.getPoints(), true);
				if(Geo.rectIntersect(visPoints, inters)) {
					simpleArc = false;
				}
			}
			
			if(simpleArc) {//simpleArc
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
					pMin = Geo.raySegIntersect(pos, minAngLim, wall.getPoints(), true);
				}
				// repeat ^
				if(Geo.angleFallsIn(maxAng, minAngLim, maxAngLim)) {
					p.arc(pos.x, pos.y, time * 2, time * 2, maxAng, maxAngLim);
				} else {
					pMax = Geo.raySegIntersect(pos, maxAngLim, wall.getPoints(), true);
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
	
	private AngVect[] orderMinMax(Point[] unorg, Point view, Wall wall) {
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
		
		AngVect min = new AngVect(pMin, minAng, wall, true);
		AngVect max = new AngVect(pMax, maxAng, wall, false);
		
		// set linked references to each other
		min.link = max;
		max.link = min;
		return new AngVect[] {min, max};
	}
	private AngVect[] orderMinMax(Point[] unorg, Point view) {
		return orderMinMax(unorg, view, null);
	}
	private AngVect[] orderMinMax(Wall wall, Point view) {
		return orderMinMax(wall.getPoints(), view, wall);
	}
	
	public ArrayList<Arc> generateArcs(String mode) {
		/*  IGNORE THIS
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
		*/
		
		ArrayList<AngVect> angs = new ArrayList<AngVect>();
		ArrayList<AngVect> started = new ArrayList<AngVect>();
		
		for(Wall wall: Wall.walls) {
			// create and add all AngVects to angs and started lists
			AngVect[] wallPoints = orderMinMax(wall, pos);
			AngVect min = wallPoints[0];
			AngVect max = wallPoints[1];
			
			if(Geo.angleFallsIn(min.angle, minAngLim, maxAngLim)) {
				angs.add(min);
			}
			
			if(Geo.angleFallsIn(max.angle, minAngLim, maxAngLim)) {
				angs.add(max);
			}
			
			// wall crosses over minAngLim
			if(Geo.angleFallsIn(minAngLim, min.angle, max.angle)) {
				started.add(min);
			}
		}
		ArrayList<Arc> subArcs = new ArrayList<Arc>();
		Collections.sort(angs);
		// rotate angs list to start after minAngLim and end before maxAngLim
		Collections.rotate(angs, getRotatePoint(angs));
		
		AngVect current; // <-- currently selected min AngVect on sweep
		float lastAngle; // <-- used to form max angle of last sub arc and min of the next

		int startAt; // <-- first index to check against. used to init for loop
		if(started.size() == 0) {
			if(angs.size() == 0) {
				// sub arcs is only this arc. End and return here
				subArcs.add(this);
				return subArcs;
			} else {
				// first subarc will be passing. From minAngLim to first in angs
				subArcs.add(new Arc(minAngLim, angs.get(0).angle, pos));
				current = angs.get(0);
				lastAngle = current.angle;
				startAt = 1;
				started.add(current); // add to started since it was skipped
			}
		} else {
			// set current to AngVect closest to pos
			current = findBest(started, minAngLim, true);
			lastAngle = minAngLim;
			startAt = 0;
		}

		for(int i = startAt; i < angs.size(); i++) {
			// cycle through all in angs: sweep check
			AngVect check = angs.get(i); // <-- current AngVect to check
			if(check.isMin) {
				// began new wall
				started.add(check);
			} else {
				// hit end of some wall
				started.remove(check.link);
			}
			
			if(check == current.link) {
				// got to end of current wall
				
				// add arc before fall
				subArcs.add(new Arc(lastAngle, check.angle, check.wall, pos));
				lastAngle = check.angle;
				if(i == angs.size() - 1) {
					break;
				}
				AngVect best = findBest(started, check.pos.slopeWith(pos));
				if(best != null) {
					// found wall to set current to
					current = best;
					// END OF drop wall type, continue;
				} else {
					// no wall to drop to, create passing arc in addition to the first
					AngVect nextAngVect = angs.get(i + 1);
					subArcs.add(new Arc(lastAngle, nextAngVect.angle, pos));
					
					current = nextAngVect;
					lastAngle = nextAngVect.angle;
					// increase index again since two arcs were made
					i++;
					started.add(angs.get(i)); // add to started since it was skipped
					// END OF drop wall with passing arc type, continue;
				}
			} else if(check.isMin) {
				// check is a possible interrupt
				if(!current.wall.intersects(pos, check.pos)) {
					// check is interrupt
					subArcs.add(new Arc(lastAngle, check.angle, current.wall, pos));
					current = check;
					lastAngle = check.angle;
					// END OF interrupt wall type, continue;
				}
			}
		}
		// add final arc completing the sweep
		AngVect best = findBest(started, maxAngLim, true);
		if(best != null) {
			// reflective arc
			subArcs.add(new Arc(lastAngle, maxAngLim, best.wall, pos));
		} else {
			// passing arc
			subArcs.add(new Arc(lastAngle, maxAngLim, pos));
		}
		
		/* IGNORE THIS
		if(tempSource) {
			// TEMP
			pos = oldPos;
			minAngLim = oldMinAngLim;
			maxAngLim = oldMaxAngLim;
		}
		*/
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
	
	private AngVect findBest(ArrayList<AngVect> started, float raySlope) {
		AngVect best = null;
		float bestDist = -1;
		
		// cycle through "started" containing all walls that have started
		for(AngVect newBest: started) {
			float newBestDist = Geo.raySegDist(pos, raySlope, newBest.pos, newBest.link.pos, true);
			if(newBestDist < bestDist || bestDist == -1) {
				best = newBest;
				bestDist = newBestDist;
			}
		}
		return best;
		
	}
	private AngVect findBest(ArrayList<AngVect> started, float rayAngle, boolean isAng) {
		if(isAng) {
			return findBest(started, (float) Math.tan(rayAngle));
		} else {
			float raySlope = rayAngle;
			return findBest(started, raySlope);
		}
		
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
