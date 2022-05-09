package alg.mf;

import java.util.Random;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import profile.Profile;
import util.reader.DatasetReader;

public class BPR 
	extends MatrixFactorisationRatingPrediction	{

	
	private Map<Integer,Integer> invUserRow;
	private Map<Integer,Integer> invItemRow;
	private int[] pool;
	
	private Random numGen ;
	
	// The trainingData array of private class TrainingTriple allows for access to
	// the training data in a convenient way. For SGD, it is necessary to access
	// the training data in a random order. Hence the training data is placed in 
	// an array, which can then be accessed at random
	
	// The trainingData will contain triples of (user, item, negative_item)
	// For the BPR algorithm, we will not directly user the rating value
	private TrainingTriple[] trainingData;
	private class TrainingTriple {

	    public final Integer user;
	    public final Integer item;
	    public Integer negativeItem;
	    public final Double rating;
	    
	    

	    public TrainingTriple(Integer user, Integer item, Double rating) {
	        this.user = user;
	        this.item = item;
	        this.rating = rating;
	        this.negativeItem = -1; // this value will be set in addNegative Samples
	    }

	}



	public BPR(DatasetReader reader, int k)
	{
		super(reader,k);
		numGen = new Random();
		setDefaultHyperParams();
		int ntrans = 0;
		
		// Set up the training data 
		// The triples will be filled with the user and item ids obtained
		// through userRow and itemRow - which return contiguous row 
		// indices from 0, 1, 2, ...
		
		
		for (Integer userId : reader.getUserIds() ) {

			Profile pu = reader.getUserProfiles().get(userId);
			ntrans += pu.getSize();
		}
		trainingData = new TrainingTriple[ntrans];
		
		ntrans=0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			for (Integer itemId : pu.getIds()) {		
				trainingData[ntrans] = 
						new TrainingTriple(userRow.get(userId),
								itemRow.get(itemId),
								(pu.getValue(itemId)));
				ntrans = ntrans+1;
			}
		}
		// we need some way to map the contiguous user and row ids
		// back to the original ids - this is done using the
		// invUserRow and invItemRow maps
		
		invUserRow = new HashMap<Integer,Integer>();
		invItemRow = new HashMap<Integer,Integer>();
		for (int u : userRow.keySet()) {
			invUserRow.put(userRow.get(u),u);
		}
		for (int i : itemRow.keySet()) {
			invItemRow.put(itemRow.get(i),i);
		}
		
		// Finally, a pool of item ids is set up 
		// to allow random item ids to be generated in a non-repetitive way
		pool = new int[Q.length];
		for (int j=0;j<Q.length;j++) {
			pool[j]=j;
		}
		
		
	}

	// The hyper-parameters of the BPR algorithm
	protected void setDefaultHyperParams()
	{
		learningRate = 0.01;
		numberPasses = 100;
		regWeightP = 0.01;
		regWeightQ = 0.01;
		regWeightItemBias = 0.01;
		numReports = 100;
	}

	private void initialise(Double[][] Mat)
	{
		for (int i=0; i<Mat.length;i++)	
			for (int j=0; j<Mat[0].length;j++) {
				Mat[i][j] = numGen.nextDouble()/Math.sqrt(K);	
			}
	}
	private void initialise(Double[] Vec)
	{
		for (int i=0; i<Vec.length;i++)	{
			Vec[i] =  numGen.nextDouble()/Math.sqrt(K);
		}
	}
	private void initialise(Double[] Vec, double val)
	{
		for (int i=0; i<Vec.length;i++)	{
			Vec[i] =  val;
		}
	}


	public void fit()
	{   
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		
		// user and global biases are not required by BPR, so
		// set them to zero
		initialise(userBias,0.0);
		globalBias = 0.0;

		int reportfreq = numReports>0 ? 
				(int)Math.ceil(numberPasses*1.0/numReports):0;

		for (int iter=0;iter<numberPasses;iter++) {
			
			// each pass, generate a new batch of negative samples
			// The addNegativeSamples function must be written (see below)
			TrainingTriple[] augmentedTrainingData = 
					addNegativeSamples(trainingData,Q.length);
			
			int ntrans = trainingData.length;

			double zuijavg = 0.0;
			for (int s=ntrans;s>0;s--) {
				
				// Draw the next sample from the augmented training data
				int draw = numGen.nextInt(s);
				TrainingTriple sample = augmentedTrainingData[draw];
				trainingData[draw] = augmentedTrainingData[s-1];
				trainingData[s-1] = sample;
				
				
				
				// Extract user, item and negativeItem from the training sample
				Integer u = sample.user;
				Integer i = sample.item;
				Integer j = sample.negativeItem;
				if (j==-1) {
					System.out.printf("%d %d : No matching negative item.\n",u,i,j);
					continue;
				}
				
				// call getPrediction to get the prediction value at (u,i) and (u,j)
				Double rhatui = getPrediction(invUserRow.get(u),invItemRow.get(i));
				Double rhatuj = getPrediction(invUserRow.get(u),invItemRow.get(j));
				
				// Follow MFSGDRatingPrediction to see how to complete this loop to
				// update
				
				// P[u][k]
				// Q[i][k]
				// Q[j][k]
				// itemBias[i]
				// itemBias[j]
				
				// zuij is the scalar part of the gradient - this should gradually
				// get smaller as the algorithm progresses
				double zuij = 1.0 / (1.0 + Math.exp((rhatui - rhatuj)));
				zuijavg += zuij;

				for(int c=0; c<K; c++) {
					double puk = P[u][c];
					double qik = Q[i][c];
					double qjk = Q[j][c];
					double qjk_bias = itemBias[j];
					double qik_bias = itemBias[i];
					
					// update the P and Q matrices
					P[u][c] = P[u][c] - learningRate * (
							(-zuij) * (qik - qjk) + regWeightP * puk);
					Q[i][c] = Q[i][c] - learningRate * (
							(-zuij) * puk + regWeightQ * qik);
					Q[j][c] = Q[j][c] - learningRate * (
							(zuij) * puk + regWeightQ * qjk);
					
					// update the item bias
					itemBias[i] = itemBias[i] - learningRate * (
							(-zuij) + regWeightItemBias * itemBias[i]);
					itemBias[j] = itemBias[j] - learningRate * (
							(zuij) + regWeightItemBias * itemBias[j]);
				}
				
				
				// Finish loop here ...
				
			}
			// Note that zuigavg should be getting smaller as the algorithm
			// progresses. If not, the learning rate or regularisation weights
			// might be too high
			if (reportfreq>0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tGradient=%f\n",iter,zuijavg/ntrans);
		}
		return;	
	}
	
	TrainingTriple[] addNegativeSamples(TrainingTriple[] trainingSet, int nitems) {
		// This function should iterate through the trainingSet data
		// Then for each trainingSet sample, it should fill the negativeItem
		// field with an item that the user HAS NOT RATED

		HashMap<Integer,ArrayList<Integer>> userItems = new HashMap<Integer,ArrayList<Integer>>();
		for (int i=0; i<trainingSet.length; i++) {
			TrainingTriple sample = trainingSet[i];
			
			Integer u = sample.user;
			Integer item = sample.item;
			Double rating = sample.rating;

			if (userItems.get(u) == null)
			{
				userItems.put(u, new ArrayList<Integer>());
			}
			userItems.get(u).add(item);
		}

		
		for (int i=0; i<trainingSet.length; i++) {
			TrainingTriple sample = trainingSet[i];
			Integer u = sample.user;
			ArrayList<Integer> items = userItems.get(u);

			// get the list of items that the user has NOT rated
			List<Integer> unratedItems = new ArrayList<Integer>();
			for (int j=0; j<nitems; j++) {
				if (!items.contains(j))
					unratedItems.add(j);
			}
			
			// randomly select one of the unrated items
			int draw = numGen.nextInt(unratedItems.size());
			sample.negativeItem = unratedItems.get(draw);
		}
		
			
		return trainingSet;
	}
	
}

