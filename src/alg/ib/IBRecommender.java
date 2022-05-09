package alg.ib;

import java.util.List;
import java.util.Set;
import alg.Recommender;
import profile.Profile;
import similarity.SimilarityMap;
import util.reader.DatasetReader;
import util.Item;

public class IBRecommender extends Recommender {
	private SimilarityMap<Item> simMap;

	public IBRecommender(final DatasetReader reader, final SimilarityMap<Item> simMap) {
		super(reader);
		this.simMap = simMap;			
	}
	
	public Profile getRecommendationScores(final Integer userId)
	{
		// User profile
		Set<Integer> itemSet = reader.getItemIds();
		Profile scores = new Profile(userId);
		Profile userProfile = reader.getUserProfiles().get(userId);
		
		for (Integer simId : itemSet) {
			double s = 0.0;

			for (Integer profId: userProfile.getIds())
			{
				Double sim = simMap.getSimilarity(profId, simId);
				if (sim!=null) {
					s += sim;
				}
			}
			scores.addValue(simId,s);
		}
		return scores;
		
	}
	

	public List<Integer> getRecommendations(final Integer userId)
	{	
		Profile userProfile = reader.getUserProfiles().get(userId);
		Profile scores = getRecommendationScores(userId);
		
		return getRecommendationsFromScores(userProfile,scores);	
	}

}
