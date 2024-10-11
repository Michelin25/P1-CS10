import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
/**
 * PS-2 provided code
 * A point quadtree: stores an element at a 2D position, with children at the subdivided quadrants
 * E extends Point2D to ensure whatever the PointQuadTree holds, it implements getX and getY
 * 
 * @author Tim Pierson, Dartmouth CS10, Winter 2024, based on prior term code
 * 
 * 
 */

//Edited by Michal Tvrdon and Aryan Bawa

public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	public E getPoint() { return point; }
	public int getX1() { return x1; }
	public int getY1() { return y1; }
	public int getX2() { return x2; }
	public int getY2() { return y2; }

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 * @return child for quadrant
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		// storing the x and y position of the point p2
		double x = p2.getX();
		double y = p2.getY();
		// Checking per quadrant 1, including the borderline of the rectangle
		if (x>=point.getX() && y <=point.getY()){
			//If quadrant empty add recursively call the insert function
			if (hasChild(1)){
				(c1).insert(p2);
			} else {
				c1 = new PointQuadtree<E>(p2,(int)point.getX(),y1,x2,(int)point.getY());
				//How do I set all children to Null
			}
		}
		//Checking the second quadrant
		if (x<point.getX() && y <point.getY()){
			//If quadrant empty add the point recursively, by using the insert function
			if (hasChild(2)){
				(c2).insert(p2);
			} else {
				//if there is no child creat a new one
				c2 = new PointQuadtree<E>(p2,(int)x1,y1,(int)point.getX(),(int)point.getY());
			}
		}
		// Same logic as the previous check this time we include the borderlines
		if (x<=point.getX() && y >=point.getY()){
			//If quadrant empty add recursively call the insert function
			if (hasChild(3)){
				(c3).insert(p2);
			} else {
				c3 = new PointQuadtree<E>(p2,x1,(int)point.getY(),(int)point.getX(),y2);
			}
		}
		//Checking if the point is in the rectangle
		if (x>point.getX() && y >point.getY()){
			//If quadrant empty add recursively call the insert function
			if (hasChild(4)){
				(c4).insert(p2);
			} else {
				c4 = new PointQuadtree<E>(p2,(int)point.getX(),(int)point.getY(),x2,y2);
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		// determining the size of three, going down the tree and adding every time we pass a node
		int num = 1;
		if (hasChild(1)) num += c1.size();
		if (hasChild(2)) num += c2.size();
		if (hasChild(3)) num += c3.size();
		if (hasChild(4)) num += c4.size();
		return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 * @return List with all points in the quadtree
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		//We first initiate a list and than call the function addToPoints(), which recursivelly travels
		List<E> f = new ArrayList<E>();
		addToPoints(f);
		return f;
	}

	private void addToPoints(List<E> pList) {
		pList.add(point);
		// as we pass a node we add it to the list, and check it's children
		if (hasChild(1)) c1.addToPoints(pList);
		if (hasChild(2)) c2.addToPoints(pList);
		if (hasChild(3)) c3.addToPoints(pList);
		if (hasChild(4)) c4.addToPoints(pList);
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	// Method used to find all the points in the circle
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		List<E> check = new ArrayList<E>();
		//Find Help is a recursive helper function
		findHelp(cx,cy,cr,check);
		return check;
	}

	public void findHelp(double cx, double cy, double cr, List<E> check){
		//First we check whether the cirlce of intrest intersects the rectangle
		if (Geometry.circleIntersectsRectangle(cx,cy,cr,x1,y1,x2,y2)){
			// if it does, we check if the point of intrest is in the circle
			if (Geometry.pointInCircle(point.getX(), point.getY(),cx,cy,cr)) {
				check.add(point);
			}
			// than we check the children of the point
			if (hasChild(1)) c1.findHelp(cx,cy,cr,check);
			if (hasChild(2)) c2.findHelp(cx,cy,cr,check);
			if (hasChild(3)) c3.findHelp(cx,cy,cr,check);
			if (hasChild(4)) c4.findHelp(cx,cy,cr,check);
		}
	}
	// TODO: YOUR CODE HERE for any helper methods

}

