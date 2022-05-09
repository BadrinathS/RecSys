package alg.ib;

import util.reader.DatasetReader;
import similarity.metric.item.ItemSimilarityLearner;
import org.apache.commons.math3.linear.*;



public class Ease extends ItemSimilarityLearner {
	
	protected double lambda;

	public Ease(DatasetReader reader)
	{
		super(reader);
		this.lambda = 1.0;
		learnItemSimilarity();
	}
	public Ease(DatasetReader reader, double lambda)
	{
		super(reader);
		this.lambda = lambda;
		learnItemSimilarity();
		
	}
	
	@Override
	public void learnItemSimilarity()
	{
		int n = R.getColumnDimension();
		int m = R.getRowDimension();


		
		// Complete this method to learn the similarity matrix
		// B[i][j] using the EASE algorithm
		RealMatrix RT =  R.transpose();
		RealMatrix G = RT.multiply(R);
		RealMatrix I = MatrixUtils.createRealIdentityMatrix(n);
		G = G.add(I.scalarMultiply(lambda));
		RealMatrix G_inverse = MatrixUtils.inverse(G);

		
		double[] B = new double[n];
		for (int i=0;i<n;i++)
			B[i]=1.0/G_inverse.getEntry(i,i);
		
		RealMatrix D = MatrixUtils.createRealDiagonalMatrix(B);

		this.B = I.subtract(G_inverse.multiply(D)).getData();

		
		
		// Complete this method to learn the similarity matrix
		// B[i][j] using the EASE algorithm
		
		
	
	}


}
