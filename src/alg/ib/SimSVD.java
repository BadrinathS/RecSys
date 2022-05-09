package alg.ib;

import util.reader.DatasetReader;
import similarity.metric.item.ItemSimilarityLearner;
import org.apache.commons.math3.linear.*;



public class SimSVD extends ItemSimilarityLearner {
	
	protected int K;
	protected double mu;

	public SimSVD(DatasetReader reader)
	{
		super(reader);
		this.K = 5;
		this.mu = 0.0;
		learnItemSimilarity();
	}
	public SimSVD(DatasetReader reader, int K)
	{
		super(reader);
		this.K = K;
		this.mu = 0.0;
		learnItemSimilarity();
		
	}
	public SimSVD(DatasetReader reader, int K,double mu)
	{
		super(reader);
		this.K = K;
		this.mu = mu;
		learnItemSimilarity();
		
	}
	
	@Override
	public void learnItemSimilarity()
	{
		int n = R.getColumnDimension();
		B = new double[n][n];
		for (int i=0;i<n;i++)
			for (int j=0;j<n;j++)
				B[i][j]=0.0;

		SingularValueDecomposition svd = new SingularValueDecomposition(this.R);
		RealMatrix S = svd.getS().getSubMatrix(0, this.K-1, 0, this.K-1);
		double sigma = S.getEntry(0,0);
		RealMatrix VT=  svd.getVT().getSubMatrix(0, this.K-1, 0, n-1);

		double s[] = new double[this.K];
 		for(int i=0;i<this.K;i++)
		 {
			s[i] = S.getEntry(i,i)*S.getEntry(i,i);
			s[i] = s[i]/(s[i]+mu);
		 }
			
		RealMatrix S_diag = MatrixUtils.createRealDiagonalMatrix(s);

		RealMatrix B_mat = VT.transpose().multiply(S_diag).multiply(VT);

		this.B = B_mat.getData();

		// Complete this method to learn the similarity matrix
		// B[i][j] using the SVD algorithm
		


	}
}
