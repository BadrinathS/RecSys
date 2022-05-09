package alg.ub.predictor;
import java.util.Set;
import profile.Profile;

import alg.ub.UBCFRatingPredictionAlg;
import neighbourhood.Neighbourhood;


public class DeviationFromUserMeanPredictor implements UBCFPredictor 
{
	/**
	 * constructor - creates a new DeviationFromMeanUserPredictor object
	 */
	
	
	public DeviationFromUserMeanPredictor()
	{
//		UBCFPredictor predictor = new DeviationFromMeanUserPredictor();

	}

	/**
	 * @returns the target user's predicted rating for the target item or null if a prediction cannot be computed
	 * @param alg - UserBasedCF algorithm
	 * @param userId - the numeric ID of the target user
	 * @param itemId - the numerid ID of the target item
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg,
			final Integer userId, final Integer itemId)
	{
		//Implement deveiation from user mean predictor in class
		//return the predicted rating for the target item
		//return null if a prediction cannot be computed

		Neighbourhood<Profile> neighbour = alg.getNeighbourhood();
		Set<Integer> neighbours = neighbour.getNeighbours(userId);
		
		Profile userProfile = alg.getReader().getUserProfiles().get(userId);

		double usermeanrating = userProfile.getMeanValue();
		double sum = 0;
		double count = 0;
//		System.out.println(neighbours);
		
		if(neighbours == null)
		{
			return null;
		}
		for(Integer neighbourId : neighbours)
		{
			
			Profile neighbourProfile = alg.getReader().getUserProfiles().get(neighbourId);
			double neighbourmeanrating = neighbourProfile.getMeanValue();
			
			if(neighbourProfile.getValue(itemId) != null)
			{
				double rating = neighbourProfile.getValue(itemId);
				sum += (rating - neighbourmeanrating);
				count++;
			}
		}
		sum+=usermeanrating;



		return sum/count;

	}
}
