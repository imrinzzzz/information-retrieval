/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1
 */

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		HashSet<Integer> trueQuery = (HashSet<Integer>) answers.get(query.getId());
		List<SearchResult> tryQuery = searcher.search(query.getRawText(), k);
		HashSet<Integer> truePositive = new HashSet<>();
		for(SearchResult s: tryQuery) {
			truePositive.add(s.getDocument().getId());
		}
		truePositive.retainAll(trueQuery);
		double[] topK_prf = new double[3];
		
		// Precision
		topK_prf[0] = tryQuery.size() > 0 ? (truePositive.size() / (double)tryQuery.size()) : 0;
		// Recall
		topK_prf[1] = trueQuery.size() > 0 ? (truePositive.size() / (double)trueQuery.size()) : 0;
		// F1
		topK_prf[2] = topK_prf[0] > 0 || topK_prf[1] > 0 ? (2*topK_prf[0]*topK_prf[1] / (topK_prf[0] + topK_prf[1])) : 0;
		
		return topK_prf;
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double[] avgPRF = new double[3];
		double[] prf;
				
		for(Document d: queries) {
			prf = getQueryPRF(d, searcher, k);
			avgPRF[0] += prf[0];
			avgPRF[1] += prf[1];
			avgPRF[2] += prf[2];
		}

		for (int i = 0; i < avgPRF.length; i++) {
			avgPRF[i] = avgPRF[i] / queries.size();
		}
				
		return avgPRF;
		/****************************************************************/
	}
}
