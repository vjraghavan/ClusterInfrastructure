package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import api.Space;

/**
 * Simulates the Client to submit Travelling salesman Task to remote Compute
 * Engine
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class EuclideanTspClient {
	private static final int N_PIXELS = 256;

	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			double cities[][] = { { 1, 1 }, { 1, 2 }, { 1, 3 }, { 1, 4 },
					{ 2, 1 }, { 2, 2 }, { 2, 3 }, { 2, 4 }, { 3, 1 }, { 3, 2 },
					{ 3, 3 }, { 3, 4 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 },
					{ 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 }, { 6, 1 }, { 6, 2 },
					{ 6, 3 }, { 6, 4 } };
			String spaceMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(spaceMachineName);
			Space space = (Space) registry.lookup(Space.SERVICE_NAME);
			long startTime = System.nanoTime();
			EuclideanTspJob euclideanTspJob = new EuclideanTspJob(cities);
			euclideanTspJob.generateTasks(space);
			int[] tour = (int[]) euclideanTspJob.getOverallResult();
			long endTime = System.nanoTime();
			System.out.println("***********Client GUI Display*************");
			long clientGUIStartTime = System.nanoTime();
			JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue(
					cities, tour);
			// display JLabels: graphic images
			JFrame frame = new JFrame("Result Visualizations");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = frame.getContentPane();
			container.setLayout(new BorderLayout());
			container
					.add(new JScrollPane(euclideanTspLabel), BorderLayout.EAST);
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
			space.shutdown();
		} catch (RemoteException re) {
			java.util.Date date = new java.util.Date();
			System.out.println("Client goes down now at :"
					+ new Timestamp(date.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Compute exception:");
		}
	}

	/**
	 * Displays Solution for Travelling Salesman Problem in a GUI
	 * 
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 * @param tour
	 *            An array containing cities in order that constitute an optimal
	 *            solution to TSP
	 * @return Returns JLabel representing solution to TSP
	 */

	private static JLabel displayEuclideanTspTaskReturnValue(double[][] cities,
			int[] tour) {
		System.out.println(tour.length);
		System.out
				.println("**************************************************");
		System.out
				.println("City Locations as perceived in Two Dimensional Space");
		System.out
				.println("**************************************************");
		for (int i = 0; i < cities.length; i++) {
			System.out.print("City" + i + "'s Location : ");
			System.out.print("(" + cities[i][0] + "," + cities[i][1] + ")");
			System.out.println();
		}
		System.out.println("\n\n*******************************************");
		System.out.println("Minimun Tour considering euclidean distances");
		System.out.println("********************************************");
		for (int i = 0; i < tour.length; i++) {
			System.out.print("City" + tour[i]);
			if (i != tour.length - 1)
				System.out.print(" --> ");
		}
		// display the graph graphically, as it were
		// get minX, maxX, minY, maxY, assuming they 0.0 <= mins
		double minX = cities[0][0], maxX = cities[0][0];
		double minY = cities[0][1], maxY = cities[0][1];
		for (int i = 0; i < cities.length; i++) {
			if (cities[i][0] < minX)
				minX = cities[i][0];
			if (cities[i][0] > maxX)
				maxX = cities[i][0];
			if (cities[i][1] < minY)
				minY = cities[i][1];
			if (cities[i][1] > maxY)
				maxY = cities[i][1];
		}

		// scale points to fit in unit square
		double side = Math.max(maxX - minX, maxY - minY);
		double[][] scaledCities = new double[cities.length][2];
		for (int i = 0; i < cities.length; i++) {
			scaledCities[i][0] = (cities[i][0] - minX) / side;
			scaledCities[i][1] = (cities[i][1] - minY) / side;
		}

		Image image = new BufferedImage(N_PIXELS, N_PIXELS,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		int margin = 10;
		int field = N_PIXELS - 2 * margin;
		// draw edges
		graphics.setColor(Color.BLUE);
		int x1, y1, x2, y2;
		int city1 = tour[0], city2;
		x1 = margin + (int) (scaledCities[city1][0] * field);
		y1 = margin + (int) (scaledCities[city1][1] * field);
		for (int i = 1; i < cities.length; i++) {
			city2 = tour[i];
			x2 = margin + (int) (scaledCities[city2][0] * field);
			y2 = margin + (int) (scaledCities[city2][1] * field);
			graphics.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;
		}
		city2 = tour[0];
		x2 = margin + (int) (scaledCities[city2][0] * field);
		y2 = margin + (int) (scaledCities[city2][1] * field);
		graphics.drawLine(x1, y1, x2, y2);

		// draw vertices
		int VERTEX_DIAMETER = 6;
		graphics.setColor(Color.RED);
		for (int i = 0; i < cities.length; i++) {
			int x = margin + (int) (scaledCities[i][0] * field);
			int y = margin + (int) (scaledCities[i][1] * field);
			graphics.fillOval(x - VERTEX_DIAMETER / 2, y - VERTEX_DIAMETER / 2,
					VERTEX_DIAMETER, VERTEX_DIAMETER);
		}
		ImageIcon imageIcon = new ImageIcon(image);
		return new JLabel(imageIcon);
	}
}