package alg.rerank;

import profile.Profile;
import similarity.SimilarityMap;
import util.ScoredThingDsc;

import java.util.List;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.SortedSet;
import java.util.TreeSet;

import util.Item;


public class DiversityReranker implements Reranker
{
	SimilarityMap<Item> distanceMap;
	
	double lambda;
	
	public DiversityReranker(SimilarityMap<Item> distanceMap, double lambda)
	{
		this.distanceMap = distanceMap;
		this.lambda = lambda;
	}
	
	
	public List<Integer> rerank(Profile userProfile,Profile scores)
	{
		
		// IMPLEMENT THE DIVERSIFICATION METHOD THAT 
		// re-ranks a set of items to maximise a tradeoff 
		// between accuracy and diversity as given by the parameter
		// lambda
		
		// BELOW IS THE CODE USED IN RECOMMENDER.JAVA to 
		// return a list of items according to their. This code is
		// added here just to give you a working starting point.
		
		// You should modify/replace this code with a code to 
		// that chooses the list according to the accuracy/diversity
		// tradeoff

		// create a list to store recommendations
		List<Integer> recs = new ArrayList<Integer>();
		List<Integer> candidate = new ArrayList<Integer>();
		List<Double> novelty = new ArrayList<Double>();
		// List<Double> temp_novelty = new ArrayList<Double>();
		List<Double> temp_scores = new ArrayList<Double>();
		
		if (scores==null)
			return recs;

		// store all scores in descending order in a sorted set
		SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>(); 
		for(Integer id: scores.getIds()) {
			double s = scores.getValue(id);
			
			if (s>0)
				ss.add(new ScoredThingDsc(s, id));
		}
		
		// save all recommended items in descending order of similarity in the list
		// but leaving out items that are already in the user's profile
		for(Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext();)
		{
			ScoredThingDsc st = iter.next();
			Integer id = (Integer)st.thing;
			if (st.score > 0.0 && !userProfile.contains(id))
			{
				candidate.add(id);
				novelty.add(0.0);
			}
				
		}


		// Adding the first element from candidate set to recommender set
		// removing the same element from novelty set since its in recommended set. 
		recs.add(candidate.get(0));
		candidate.remove(0);
		novelty.remove(0);

		// Initialising all the values
		double es = 0.0;
		double en = 0.0;
		double sigma_s = 0.0;
		double sigma_n = 0.0;
		double temp_suj = 0.0;
		double temp_nuj = 0.0;

		
		//making list of maximum 50 recommended items instead of all the items.
		//Since implementing for all candidate set will take too much time. 
		while (recs.size()<50)
		{	
		
			double meanscore = 0.0;
			double meannovelty = 0.0;
			

			for(Integer id: candidate)
			{
				// Updating novelty by calculating the distance between each candidate item with newly added recommended set item
				novelty.set(candidate.indexOf(id), novelty.get(candidate.indexOf(id)) + distanceMap.getSimilarity(id, recs.get(recs.size()-1)));
				
				// Printing out values to check if they are correct
				System.out.print("Recs "+recs+"\n");
				System.out.print("Id " + id+"\n");
				System.out.print("Distance " +distanceMap.getSimilarity(id, recs.get(recs.size()-1))+"\n");

				// Calculating mean score and novelty as per the algorithm
				// Calculating es and en
				meanscore += scores.getValue(id);
				meannovelty +=  novelty.get(candidate.indexOf(id));
				es += Math.pow(scores.getValue(id), 2);
				en += Math.pow(novelty.get(candidate.indexOf(id)), 2);
			}

		
			// Computing mean values and sigma values 
			meanscore = meanscore / candidate.size();
			meannovelty = meannovelty / novelty.size();
			sigma_s += es/candidate.size() - meanscore*meanscore;
			sigma_n += en/novelty.size() - meannovelty*meannovelty;


			// Initialising variables to find the item id with maximum updated score
			double max_rating = 0;
			Integer arg_max_id = 0;

			for(Integer id: candidate)
			{
				// Updated score of item id considering previous score and novelty
				temp_suj = (scores.getValue(id) - meanscore)/sigma_s;
				temp_nuj = (novelty.get(candidate.indexOf(id))-meannovelty)/sigma_n;
				temp_suj = (1-lambda)*temp_suj + lambda*temp_nuj;

				// Finding the item id with maximum updated score
				if (temp_suj > max_rating)
				{
					max_rating = temp_suj;
					arg_max_id = id;
				}
				
			}

			System.out.print("arg_max_id: "+arg_max_id+"\n");
			System.out.print("candidate.indexOf(arg_max_id): "+candidate.indexOf(arg_max_id)+"\n");
			System.out.print("candidate.contains(arg_max_id): "+candidate.contains(arg_max_id)+ "\n");
			System.out.print("novelty.size(): "+novelty.size()+"\n");

			// Adding the item id with maximum updated score to recommended	set
			// Removing the same item from candidate set and novelty set. 
			recs.add(arg_max_id);
			novelty.remove(candidate.indexOf(arg_max_id));
			candidate.remove(arg_max_id);
		}
	
		return recs;
	}
}
