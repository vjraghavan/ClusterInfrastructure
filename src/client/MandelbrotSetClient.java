package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import api.Space;

/**
 * Simulates the Client to submit MandelbrotSet Task to remote Compute Engine
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class MandelbrotSetClient {

	private static final double lowerX = -0.7510975859375;
	private static final double lowerY = 0.1315680625;
	private static final double edgeLength = 0.01611 / 1024;
	private static final int N_PIXELS = 1024;
	private static final int ITERATION_LIMIT = 4096;
	private static Color[] colors;

	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			colors = new Color[ITERATION_LIMIT];
			computeColors();
			String spaceMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(spaceMachineName);
			Space space = (Space) registry.lookup(Space.SERVICE_NAME);
			long startTime = System.nanoTime();
			MandelbrotSetJob mandelbrotSetJob = new MandelbrotSetJob(lowerX,
					lowerY, edgeLength, N_PIXELS, N_PIXELS, ITERATION_LIMIT);
			mandelbrotSetJob.generateTasks(space);
			mandelbrotSetJob.collectResults(space);
			int[][] counts = mandelbrotSetJob.getOverallResult();
			long endTime = System.nanoTime();
			System.out.println("***********Client GUI Display*************");
			long clientGUIStartTime = System.nanoTime();
			JLabel mandelbrotLabel = displayMandelbrotSetTaskReturnValue(counts);
			// display JLabels: graphic images
			JFrame frame = new JFrame("Result Visualizations");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = frame.getContentPane();
			container.setLayout(new BorderLayout());
			container.add(new JScrollPane(mandelbrotLabel));
			frame.pack();
			long clientGUIEndTime = System.nanoTime();
			frame.setVisible(true);
			System.out.println("Client GUI display Start Time :"
					+ clientGUIStartTime);
			System.out.println("Client GUI display Start Time :"
					+ clientGUIEndTime);
			System.out.println("Client GUI display Elapsed Time :"
					+ (clientGUIEndTime - clientGUIStartTime));
			System.out.println(" Total runTime as seen by the Client :"
					+ (endTime - startTime));
		} catch (Exception e) {
			System.err.println("Compute exception:");
			e.printStackTrace();
		}
	}

	/**
	 * Displays Solution for MandelbrotSet Problem in a GUI
	 * 
	 * @param counts
	 *            An array containing results of whether each point in the
	 *            complex plane representing a pixel belongs to MandelbrotSet or
	 *            not
	 * 
	 * @return Returns JLabel representing solution to MandelbrotSet
	 */

	private static JLabel displayMandelbrotSetTaskReturnValue(int[][] counts) {
		Image image = new BufferedImage(N_PIXELS, N_PIXELS,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		for (int i = 0; i < counts.length; i++)
			for (int j = 0; j < counts.length; j++) {
				graphics.setColor(getColor(counts[i][j]));
				graphics.fillRect(i, j, 1, 1);
			}
		ImageIcon imageIcon = new ImageIcon(image);
		return new JLabel(imageIcon);
	}

	/**
	 * Matches colour for corresponding iteration limit at which MandelbrotSet
	 * computation of a particular pixel ends
	 * 
	 * @param i
	 *            Represents the iteration point at which MandelbrotSet
	 *            computation ends for that particular pixel
	 * 
	 * @return Returns a specific colour according to the iteration level it
	 *         reached
	 */

	private static Color getColor(int i) {
		if (i == ITERATION_LIMIT)
			return Color.BLACK;
		else
			return colors[i];
	}

	/**
	 * Computes colours for representing MandelbrotSet solution as a spectrum of
	 * colours
	 * 
	 */

	private static void computeColors() {
		int i = 0;
		for (float red = 0; red < 1.0; red += 1.0 / 8.0) {
			for (float green = 0; green < 1.0; green += 1.0 / 8.0) {
				for (float blue = 0; blue < 1.0; blue += 1.0 / 8.0) {
					Color c;
					if (red == 0 && blue == 0 && green == 0)
						c = Color.white;
					else
						c = new Color(red, green, blue);
					colors[i] = c;
					i++;
				}
			}
		}
	}
}
