import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Code provided for PS-1
 * Webcam-based drawing
 * Dartmouth CS 10, Fall 2024
 *
 * @author Tim Pierson, Dartmouth CS10, Fall 2024 (based on CamPaint from previous terms)
 */
public class CamPaint extends VideoGUI {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * VideoGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void handleImage() {
		ArrayList<Point> markerSet = new ArrayList<>();
		//BufferedImage recolImg = image;
		System.out.println(targetColor);
		if (targetColor !=null) {
			finder = new RegionFinder(image);
			finder.findRegions(targetColor);
			markerSet = finder.largestRegion();

			if (displayMode == 'w') {
			}else if (displayMode == 'p') {

				System.out.println("Painting");
					if (markerSet != null) {
						//System.out.println(markerSet);
						for (Point point : markerSet) {
							painting.setRGB(point.x, point.y, paintColor.getRGB());
						}
					}
				image = painting;
			}else if (displayMode == 'r') {
				finder.recolorImage();
				System.out.println(finder.getRecoloredImage());
				image = finder.getRecoloredImage();
			}
		}
		System.out.println(displayMode + " markerSet:" + markerSet);
		super.setImage1(image);
	}

// it quits randomly, no error message
//If I loose track of the card it quits

	/**
	 * Overrides the Webcam method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
			//testing the pointer
			targetColor = new Color(image.getRGB(x,y));
	}

	/**
	 * Webcam method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		new CamPaint();
	}
}
