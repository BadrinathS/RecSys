package examples;

import java.io.File;
import java.util.HashMap;

//import alg.mf.MatrixFactorisationRatingPrediction;
import alg.mf.MFGradientDescentRatingPredictionAlg;
import alg.mf.MFSGDRatingPredictionAlg;
//import alg.mf.MFSGDRatingPredictionAlg;
import alg.mf.WMFSGDRatingPredictionAlg;

import alg.RatingPredictionRecommender;
import util.evaluator.*;
import util.reader.DatasetReader;

public class MFPerformanceEvaluation {

	public static void main(String[] args)
	{


		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
	
		////////////////////////////////////////////////////////////////////////////
		// Read data into a reader object
		///////////////////////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);

		int nusers = 200;
		int seed = 0;
		int N = 10;	
		int latentSpaceDim = 20;

		// HashMap<String, Integer> genreDimMap = new HashMap<String, Integer> ();
		// int numItems = reader.getItemIds().size();
		// int numGenres = reader.getGenres().size();

		// int[][] itemMask = new int[numItems][numGenres];
		// for (int i=0; i< numItems; i++){
		// 	for (int j=0; j< numGenres; j++){
		// 		itemMask[i][j]=0;
		// 	}
		// }

		// int gindx = 0;
		// for (String g: reader.getGenres()){
		// 	genreDimMap.put(g, gindx);
		// }


		// for (Integer itemId: reader.getItemIds()){
		// 	// int i = itemRow.get(itemId);
		// 	for (String g: reader.getItem(itemId).getGenres()){
		// 		gindx = genreDimMap.get(g);
		// 		itemMask[itemId][gindx]=1;
		// 	}
		// }
		
		WMFSGDRatingPredictionAlg alg = new WMFSGDRatingPredictionAlg(reader, latentSpaceDim);
		MFGradientDescentRatingPredictionAlg alg_mf = new MFGradientDescentRatingPredictionAlg(reader, latentSpaceDim);
		
		// double[] alphas = {1.0,2.0,3.0};
		double[] alphas = {0};
		// int[] hs = {1,2,3,4,5,6,7,8};
		int[] hs = {0};
		int[] dims = {2, 3, 5, 10, 15, 20, 25, 30, 50, 80, 100};

		// Set number of times fit() writes to the console as optimisation proceeds
		// alg.setNumReports(0);

		RatingPredictionRecommender recalg = 
				new RatingPredictionRecommender(reader, alg_mf);

		// Evaluate the top-N performance of the algorithm
		Evaluator evalrec = new Evaluator(recalg,reader,N,nusers,seed);


		// for (double alpha :alphas) {

			// alg.setAlpha(alpha);
			// for (int h : hs) {
				// alg.setNegativeSamplingRate(h);

				for (int dim : dims) {
					alg_mf.setLatentSpaceDim(dim);
					alg_mf.fit();

					double prec = evalrec.aggregratePerformance(new NoveltyPrecision());
					double recall = evalrec.aggregratePerformance(new Recall());

					System.out.printf("N: %d, dim: %d, "
							+ "Prec: %.4f,Recall: %.4f\n",
							N,dim,prec,recall);


					// System.out.printf("N: %d, alpha: %.2f, h: %d, dim: %d, "
					// 		+ "Prec: %.4f,Recall: %.4f\n",
					// 		N,alpha,h,dim,prec,recall);
				// }
			// }

		}
	}
}

