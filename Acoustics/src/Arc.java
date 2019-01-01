import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

/*
 * Current Bugs:
 *		* Understood and being fixed
 *		• Still looking into source of issue
 *
 * 		generateArcs()
 *			• having right angles for min and max angle limits causes issues
 *			* closed shapes formed by walls sometimes seem to have small
 *				holes in connections or create large arcs that ignore some walls
 */

public class Arc {
	
	// Arcs are used to visualize pressure waves
	
	public static ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	private static PApplet p;
	private Point pos;
	private String type;
	
	private float minAngLim;
	private float maxAngLim;
	
	float perpDist;
	
	private Wall wall;
	private Wall rebound;
	
	public Arc(float minAngLim, float maxAngLim, Wall wall, Point pos, String type) {
		this.minAngLim = Geo.simpAngle(minAngLim);
		this.maxAngLim = Geo.simpAngle(maxAngLim);
		this.wall = wall;
		this.pos = pos;
		this.type = type;
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
		/*
		//DEBUG
		float g = 0;
		if(wall != null) {
			g = wall.color[0] / 10f;
		}
		p.ellipse(pos.x, pos.y, 5 + g, 5 + g);
		*/
		
		float outerMin = minAngLim;
		float innerMin = -1; // <-- -1 used as flag
		float innerMax = -1;
		float outerMax = maxAngLim;
		
		// stuff for rebound walls
		if(rebound != null) {
			// arc is not from source
			Point[] rebInters = Geo.segCircIntersects(rebound.getPoints(), pos, time);
			if(rebInters == null) {
				// arc has not traveled far enough to display
				return;
			}
			AngVect[] rebOrd = orderMinMax(rebInters, pos);
			float minAng = rebOrd[0].angle;
			float maxAng = rebOrd[1].angle;
			if(!minLimFallsIn(minAng, maxAng) && !maxLimFallsIn(minAng, maxAng)) {
				// arc still has not traveled far enough to display
				return;
			}
			if(angleInMinMax(minAng)) {
				outerMin = minAng;
			}
			if(angleInMinMax(maxAng)) {
				outerMax = maxAng;
			}
		}
		
		// stuff for walls that arcs crash into
		if(!type.equals("passing")) {
			Point[] wallInters =  Geo.segCircIntersects(wall.getPoints(), pos, time);
			if(wallInters != null) {
				AngVect[] wallOrd = orderMinMax(wallInters, pos);
				float minAng = wallOrd[0].angle;
				float maxAng = wallOrd[1].angle;
				if(minLimFallsIn(minAng, maxAng) && maxLimFallsIn(minAng, maxAng)) {
					// arc has traveled too far to display
					return;
				}
				if(angleInMinMax(minAng)) {
					innerMin = minAng;
				}
				if(angleInMinMax(maxAng)) {
					innerMax = maxAng;
				}
			}
		}
		
		p.pushStyle();
			p.noFill();
			if(rebound != null) {
				int[] r = rebound.color;
				p.stroke(r[0], r[1], r[2]);
			} else {
				p.stroke(0);
			}
			arcDisplay(time - 1f, outerMin, innerMin, innerMax, outerMax);
			
			// assign colors and draw final arcs
			if(!type.equals("passing")) {
				int[] w = wall.color;
				p.stroke(w[0], w[1], w[2]);
			} else {
				p.stroke(255);
			}
			
			arcDisplay(time, outerMin, innerMin, innerMax, outerMax);
		p.popStyle();
	}
	private void arcDisplay(float time, float outerMin, float innerMin, float innerMax, float outerMax) {
		if(innerMin == -1 && innerMax == -1) {
			// single arc
			arc(pos.x, pos.y, time * 2, outerMin, outerMax);
		} else {
			// partial arcs
			if(innerMin != -1) {
				arc(pos.x, pos.y, time * 2, outerMin, innerMin);
			}
			if(innerMax != -1) {
				arc(pos.x, pos.y, time * 2, innerMax, outerMax);
			}
		}
	}
	
	public boolean hasCrossed(float time) {
		// Returns whether or not this arc has crossed its wall
		return hasDone(time, "cross");
	}
	
	public boolean hasTouched(float time) {
		// Returns whether or not this arc has touched its wall
		return hasDone(time, "touch");
	}
	
	private boolean hasDone(float time, String action) {
		if(!type.equals("passing")) {
			Point[] wallInters =  Geo.segCircIntersects(wall.getPoints(), pos, time);
			if(wallInters != null) {
				AngVect[] wallOrd = orderMinMax(wallInters, pos);
				float minAng = wallOrd[0].angle;
				float maxAng = wallOrd[1].angle;
				if(action.equals("cross")) {
					// extension of  hasCrossed() function
					if(minLimFallsIn(minAng, maxAng) && maxLimFallsIn(minAng, maxAng)) {
						return true;
					}
				} else if(action.equals("touch")){
					// extension of hasTouched() function
					if(angleInMinMax(minAng) || angleInMinMax(maxAng) || minLimFallsIn(minAng, maxAng)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static int blend(int start, int with, int weight) {
		int result = start * weight;
		result += with;
		result /= weight + 1;
		
		return result;
	}
	
	private boolean angleInMinMax(float angle) {
		return Geo.angleFallsIn(angle, minAngLim, maxAngLim);
	}
	private boolean minLimFallsIn(float minAng, float maxAng) {
		return Geo.angleFallsIn(minAngLim, minAng, maxAng);
	}
	private boolean maxLimFallsIn(float minAng, float maxAng) {
		return Geo.angleFallsIn(maxAngLim, minAng, maxAng);
	}
	/*
	public void showWithFill(float time) {
		// Mostly used for debugging
		p.pushStyle();
		if(!type.equals("passing")) {
			// reflective arc
			
			int[] a = wall.color;
			//p.fill(a[0], a[1], a[2], 60);
			p.noFill();
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
				arc(pos.x, pos.y, time * 2, minAngLim, maxAngLim);
			} else {
				// arc is split into 3 pieces: 2 small arcs, 1 triangle
				AngVect[] limits = orderMinMax(inters, pos);
				Point pMin = limits[0].pos;
				Point pMax = limits[1].pos;
				float minAng = limits[0].angle;
				float maxAng = limits[1].angle;
				
				if(Geo.angleFallsIn(minAng, minAngLim, maxAngLim)) {
					// ensure intersection point is touching wall
					arc(pos.x, pos.y, time * 2, minAngLim, minAng);
				} else {
					// if not, don't draw arc part and make triangle use wall end point
					pMin = Geo.raySegIntersect(pos, minAngLim, wall.getPoints(), true);
				}
				// repeat ^
				if(Geo.angleFallsIn(maxAng, minAngLim, maxAngLim)) {
					arc(pos.x, pos.y, time * 2, maxAng, maxAngLim);
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
			arc(pos.x, pos.y, time * 2, minAngLim, maxAngLim);
		}
		p.popStyle();
	}
	*/
	
	
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
	
	public ArrayList<Arc> generateArcs() {
		ArrayList<AngVect> angs = new ArrayList<AngVect>();
		ArrayList<AngVect> started = new ArrayList<AngVect>();
		int wallOrient = 0;
		
		if(rebound != null) {
			wallOrient = Geo.orientation(rebound.getPoints(), pos);
		}
		
		for(Wall wallCheck: Wall.walls) {
			// create and add all AngVects to angs and started lists
			
			AngVect[] wallPoints = orderMinMax(wallCheck, pos);
			AngVect min = wallPoints[0];
			AngVect max = wallPoints[1];
			
			if(rebound != null && isBehindRebound(wallPoints, wallOrient)) {
				// wall is invalid and should be skipped
				continue;
			}
			
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
		
		if(rebound != null) {
			// used for drawing arcs to know where to start
			for(Arc arc: subArcs) {
				arc.setRebound(rebound);
			}
		}
		return subArcs;
	}
	
	public Arc mirrorArc() {
		Point newPos = Geo.boost(pos, wall.getPerpPoint(pos));
		float newMin = Geo.angleBoost(maxAngLim, wall.getAngle());
		float newMax = Geo.angleBoost(minAngLim, wall.getAngle());
		Arc mirror = new Arc(newMin, newMax, newPos);
		mirror.setRebound(wall);
		return mirror;
	}
	
	private boolean isBehindRebound(AngVect[] tests, int wallOrient) {
		// returns whether or not testWall is behind rebound and should not be considered
		if(tests[0].wall == rebound) {
			return true;
		}
		for(AngVect test: tests) {
			if(wallOrient == Geo.orientation(rebound.getPoints(), test.pos)) {
				return true;
			}
		}
		
		return false;
	}
	
	private int getRotatePoint(ArrayList<AngVect> list) {
		// Returns an index to rotate list by
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).angle > minAngLim) {
				return list.size() - i;
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
	
	public static void arc(float x, float y, float r, float min, float max) {
		// used instead of just calling p.arc since is max is less than min, nothing displays
		if(max < min) {
			max += Math.PI * 2;
		}
		p.arc(x, y, r, r, min, max);
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
	
	private void setRebound(Wall rebound) {
		this.rebound = rebound;
	}
}
