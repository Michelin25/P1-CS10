import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.*;
import java.lang.Math;

//import static sun.jvm.hotspot.runtime.BasicObjectLock.size;

/**
 * Code provided for PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Fall 2024
 *
 * @author Tim Pierson, Dartmouth CS10, Fall 2024, based on prior terms RegionFinder
 */
public class RegionFinder {
    private static final int maxColorDiff = 40;				// how similar a pixel color must be to the target color, to belong to a region
    private static final int minRegion = 50; 				// how many points in a region to be worth considering

    private BufferedImage image;                            // the image in which to find regions
    private BufferedImage recoloredImage;                   // the image with identified regions recolored

    private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
    // so the identified regions are in a list of lists of points

    public RegionFinder() {this.image = null;}
    public RegionFinder(BufferedImage image) {
        this.image = image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    //
    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getRecoloredImage() {
        return recoloredImage;
    }


    /**
     * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
     */
    public void findRegions(Color targetColor) {
        //System.out.println("this prints");
        // create a completely black image to help track what has and hasn't been visited
        BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        //System.out.println("what");
        regions = new ArrayList<>();



        // loop through every pixel in the image
        for (int y = 0; y < image.getHeight(); y++) {
            //System.out.println("frst for loop");
            for (int x = 0; x < image.getWidth(); x++) {

                // check if the pixel has not been visited and if the color matches
                if (visited.getRGB(x,y) == 0 && (colorMatch(targetColor, new Color(image.getRGB(x,y))))) {

                    // create a new region
                    ArrayList<Point> newRegion = new ArrayList<>();
                    Point p = new Point(x, y);

                    // tracking which pixels need to be visited
                    ArrayList<Point> toVisit = new ArrayList<>();
                    toVisit.add(p);

                    // as long as there are still pixels to visit, add them to the new region
                    while (!toVisit.isEmpty()) {
                        Point currentPoint = toVisit.get(0);
                        newRegion.add(currentPoint);
                        toVisit.remove(0);

                        // check 8-adjacency neighbors; nx,ny are neighbor coords
                        for (int ny = Math.max(0, currentPoint.y - 1);
                             ny <= Math.min(image.getHeight()-1, currentPoint.y + 1);
                             ny++) {
                            for (int nx = Math.max(0, currentPoint.x - 1);
                                 nx <= Math.min(image.getWidth()-1, currentPoint.x + 1);
                                 nx++) {
                                if (visited.getRGB(nx,ny) == 0 && colorMatch(targetColor, new Color(image.getRGB(nx, ny)))) {
                                    toVisit.add(new Point(nx, ny));
                                    visited.setRGB(nx, ny, 1);
                                }
                            }
                        }
                    }
                    if (newRegion.size() >= minRegion) {
                        regions.add(newRegion);
                    }
                }
            }
        }
    }

    /**
     * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
     */
    protected static boolean colorMatch(Color c1, Color c2) {
        int colorDiff = (int) Math.sqrt(Math.pow((c1.getRed() - c2.getRed()),2)
                + Math.pow((c1.getGreen() - c2.getGreen()),2)
                + Math.pow(c1.getBlue() - c2.getBlue(), 2));

        return colorDiff <= maxColorDiff;

//		return (Math.abs(c1.getRed() - c2.getRed()) <= maxColorDiff &&
//				Math.abs(c1.getBlue() - c2.getBlue()) <= maxColorDiff &&
//				Math.abs(c1.getGreen() - c2.getGreen()) <= maxColorDiff);
    }

    /**
     * Returns the largest region detected (if any region has been detected)
     */
    public ArrayList<Point> largestRegion() {
        if (regions.size() !=0) {
            int maxSize = 0;
            int maxReg = 0;
            for (int i = 1; i < regions.size(); i++) {
                if (regions.get(i).size() > maxSize) {
                    maxSize = regions.get(i).size();
                    maxReg = i;
                }
            }
            return regions.get(maxReg);
        }
        else {
            return null;
        }
	}

    /**
     * Sets recoloredImage to be a copy of image,
     * but with each region a uniform random color,
     * so we can see where they are
     */
    public void recolorImage() {
        // First copy the original
        recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
        // Now recolor the regions in it
        System.out.println(regions.size());
        if (regions.size() != 0) {
            for (ArrayList<Point> region : regions) {
                Color randColor = new Color((int) (Math.random() * 0x1000000));
                Color red = new Color(255, 0, 0);
                for (Point p : region) {
                    recoloredImage.setRGB(p.x, p.y, randColor.getRGB());
                }
            }
        }
        else {
            recoloredImage = image;
        }
    }
}
