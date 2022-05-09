/**
 * Compute the Mean Squared Difference similarity between profiles.
 */

package similarity.metric.profile;

import java.util.Set;
import similarity.metric.SimilarityMetric;

import profile.Profile;

public class MeanSquaredDifferenceMetric implements SimilarityMetric<Profile>
{
	private double rmax;
	private double rmin;
	/**
	 * constructor - creates a new DifferenceMetric object
	 */
	public MeanSquaredDifferenceMetric(double a,double b)
	{
		this.rmin = a;
		this.rmax = b;
		
		/* enforce a difference of 1 if the two bounds are equal */
		if (a==b)
			this.rmax = b+1;
	}
		
	/**
	 * computes the similarity between profiles
	 * @param profile 1 
	 * @param profile 2
	 */
	@Override
	public double getSimilarity(final Profile p1, final Profile p2)
	{	
		//Calculate mean squared difference in ratings between user 1 and user 2
		double sum = 0;
		Set<Integer> ids1 = p1.getIds();
		Set<Integer> ids2 = p2.getIds();
		Integer denomInteger = 0;
		double rmax = this.rmax, rmin = this.rmin;
		for (Integer id1 : ids1)
		{
			if (ids2.contains(id1))
			{
				double r1 = p1.getValue(id1);
				double r2 = p2.getValue(id1);
				sum += Math.pow(r1-r2,2);
				denomInteger += 1;
				if (r1 > rmax)
					rmax = r1;
				else if (r2 > rmax)
					rmax = r2;
				if (r1 < rmin)
					rmin = r1;
				else if (r2 < rmin)
					rmin = r2;
			}
		}
		double DifferenceMetric = sum/denomInteger;
		double SimilarityMetric = 1 - (DifferenceMetric/(rmax-rmin));

		
		return SimilarityMetric;
	}
}
