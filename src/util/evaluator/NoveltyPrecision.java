package util.evaluator;
import java.util.List;
import profile.Profile;

public class NoveltyPrecision implements TestPerfInterface{
	private Double threshold;
	Profile nov;

	public NoveltyPrecision(Double threshold, Profile nov)
	{
		this.threshold = threshold;
		this.nov = nov;
	}
	public NoveltyPrecision()
	{
		this.threshold = 1.0;
	}

	public Double testperf(Integer userId, Profile testProfile, List<Integer> recs, Integer k){
		Double prec = 0.0;
		Integer numTestItems = 0;
		for (Integer testItem: testProfile.getIds()){
			Double novelty = nov.getValue(testItem);
			if (novelty>=threshold)
				numTestItems++;
		}

		if (numTestItems==0)
			return null;

		Integer numRecItems = 0;
		for (Integer itemId: recs){
			Double val = testProfile.getValue(itemId);
			if (val != null){
				Double novelty = nov.getValue(itemId);
				if (novelty>=threshold)
					prec = prec+1.0;
			}
			numRecItems ++;
			if(numRecItems == k)
				break;
		}
		prec = prec/k;
		return prec;
	}
}
