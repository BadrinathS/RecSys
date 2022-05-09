package alg.rerank;
import profile.Profile;
import java.util.List;

public interface Reranker {
	
	
	public List<Integer> rerank(Profile userProfile, Profile scores);

}
