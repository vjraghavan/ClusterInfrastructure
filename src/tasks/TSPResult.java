package tasks;

import java.io.Serializable;

/**
 * Represents the result of the TSP computation which contains both the minimum
 * tour and the Minimum tour cost
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class TSPResult implements Serializable {
	private static final long serialVersionUID = 2907842016060277603L;
	private int[] minTour;
	private Double minDistance;

	/**
	 * 
	 * @param minTour
	 *            Represents the sequence of cities that composes a min tour
	 * @param minDistance
	 *            Represents the total distance for the entire tour
	 */
	public TSPResult(int[] minTour, Double minDistance) {
		super();
		this.minTour = minTour;
		this.minDistance = minDistance;
	}

	/**
	 * Represents the java hashcode of this object
	 * 
	 * @return Returns the hash value of this object
	 */
	public int hashCode() {
		int hashTour = minTour != null ? minTour.hashCode() : 0;
		int hashDistance = minDistance != null ? minDistance.hashCode() : 0;
		return (hashTour + hashDistance) * hashDistance + hashTour;
	}

	/**
	 * Overrides the java object's equal method
	 * 
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof TSPResult) {
			TSPResult otherPair = (TSPResult) other;
			return ((this.minTour == otherPair.minTour || (this.minTour != null
					&& otherPair.minTour != null && this.minTour
						.equals(otherPair.minTour))) && (this.minDistance == otherPair.minDistance || (this.minDistance != null
					&& otherPair.minDistance != null && this.minDistance
						.equals(otherPair.minDistance))));
		}
		return false;
	}

	/**
	 * Overrides the java toString() method
	 */
	@Override
	public String toString() {
		return "(" + minTour + ", " + minDistance + ")";
	}

	/**
	 * Gets the Minimum Tour containing sequence of cities in order which
	 * composes minimum tour
	 * 
	 * @return Returns the Minimum Tour containing sequence of cities in order
	 *         which composes minimum tour
	 */
	public int[] getMinTour() {
		return minTour;
	}

	/**
	 * Gets the min Distance of the minimum tour of the cities
	 * 
	 * @return Returns the min Distance of the minimum tour of the cities
	 */
	public Double getMinDistance() {
		return minDistance;
	}
}
