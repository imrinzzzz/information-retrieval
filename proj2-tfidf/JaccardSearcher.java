/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<String> tokenised = tokenize(queryString);
		double j_score = 0;
		int allTerm_num = 0, sameTerm_num = 0;
		SearchResult temp;
		LinkedList<SearchResult> result = new LinkedList<>();

		for(Document d: documents) {
			HashSet<String> allTerm = new HashSet<>();
			HashSet<String> sameTerm = new HashSet<>();
			allTerm.addAll(tokenised);
			sameTerm.addAll(tokenised);

			allTerm.addAll(d.getTokens());
			sameTerm.retainAll(d.getTokens());

			allTerm_num = allTerm.size();
			sameTerm_num = sameTerm.size();

			j_score = sameTerm_num == 0 ? 0 : sameTerm_num / (double)allTerm_num;
			temp = new SearchResult(d, j_score);
			result.add(temp);
		}
		Collections.sort(result);
		return result.subList(0, k);
		/***********************************************/
	}

}
