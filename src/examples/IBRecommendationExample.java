package examples;

import java.io.File;
import java.text.DecimalFormat;

import alg.ib.IBRecommender;
import util.reader.DatasetReader;
import similarity.SimilarityMap;
import similarity.metric.SimilarityMetric;
import util.Item;
import util.evaluator.Evaluator;
import util.evaluator.Precision;
import util.evaluator.Recall;
import alg.ib.Ease;
import alg.ib.SimSVD;

public class IBRecommendationExample {
	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";	
		////////////////////////////////////////////////////////////////////////////
		// Read data into a reader object
		///////////////////////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);

		
		
		////////////////////////////////////////////////////////
		int N = 20; // the number of recommendations to be made for each target user
		int nusers = 500; // number of users to evaluate
		double lambda = 0.01; // regularisation parameter for Ease
		double mu = 0.00; // regularisation parameter for simSVD
		int K = 5; // regularisation paramter for simSVD
		int whichmethod = 0; // 0 for simSVD; 1 for Ease
		
		if (args.length>0)
			N = Integer.parseInt(args[0]);
		if (args.length>1)
			whichmethod = Integer.parseInt(args[1]);
		if (args.length>2)
			mu = Double.parseDouble(args[2]);
		if (args.length>3)
			lambda = Double.parseDouble(args[3]);
		if (args.length>4)
			K = Integer.parseInt(args[4]);
		if (args.length>5)
			nusers = Integer.parseInt(args[5]);
		
		System.out.print("N="+N);
		System.out.print("\tmu="+mu);
		System.out.print("\tlambda="+lambda);
		System.out.print("\tK="+K);
		System.out.println("\tnusers="+nusers);
		
		
		DecimalFormat fmt = new DecimalFormat("#.#####");
		
		////////////////////////////////////////////////////////
		// Constructing an Ease or SimSVD object to learn the similarity metric 
		SimilarityMetric<Item> metric = null;
		
		switch (whichmethod) {
		
		case 0:
			System.out.println("Ease Similarity learner");
			metric = new Ease(reader,lambda);
			break;
		case 1:
			System.out.println("SimSVD Similarity learner");
			metric = new SimSVD(reader,K,mu);
		}

		SimilarityMap<Item> simMap = 
				new SimilarityMap<Item>(reader.getItems(),metric);	
		System.out.println("popularity,precision,recall");
		IBRecommender alg =
				new IBRecommender(reader,simMap);


		// Evaluate Recommender
		Evaluator eval = new Evaluator(alg,reader,N,nusers);
		System.out.println(  
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
	
	}


}
