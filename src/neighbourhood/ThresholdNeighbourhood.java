/**
 * A class that implements the "threshold neighbourhood" technique (user-based CF).
 */

package neighbourhood;
import profile.Profile;
import similarity.SimilarityMap;


public class ThresholdNeighbourhood<T> extends Neighbourhood<T>
{
	private final double threshold; // the number of neighbours in the neighbourhood
	
	/**
	 * constructor - creates a new ThresholdNeighbourhood object
	 * @param k - the number of neighbours in the neighbourhood
	 */
	public ThresholdNeighbourhood(final double threshold)
	{
		super();
		this.threshold = threshold;
	}
	/**
	 * stores the neighbourhoods for all ids in member Neighbour.neighbourhoodMap
	 * @param simMap - a map containing user-user similarities
	 */
	public void computeNeighbourhoods(final SimilarityMap<T> simMap)
	{


		// Currently just adds all ids to the neighbourhoods of all other ids
		// THIS NEEDS TO BE IMPLEMENTED CORRECTLY



		for (Integer simId: simMap.getIds()) { 
			Profile profile = simMap.getSimilarities(simId);
			for (Integer id: profile.getIds()) {
				if (id != simId) {
					if (simMap.getSimilarity(id, simId) > threshold) {
						this.add(simId, id);
					}
				}
				
			}
		}
	}
}
