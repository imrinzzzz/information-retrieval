/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1
 */

import java.util.*;

public class myCoolSearcher extends Searcher {
    HashMap<Document, Integer> docDict = new HashMap<>();
    HashMap<String, Integer> termDict = new HashMap<>();
    HashMap<Integer, Double> idf = new HashMap<>();
    HashMap<Integer, double[]> TFIDF = new HashMap<Integer, double[]>();
    HashMap<Integer, Double> L2Norm = new HashMap<>();

    HashMap<Integer, Double> check = new HashMap<>();

    public myCoolSearcher(String docFilename) {
        super(docFilename);
        /************* YOUR CODE HERE ******************/

        /***********************************************
         Explain the variables:
         ===Global variables===
         - docDict: hash map (unique document -> document id)
         - termDict: hash map (unique term -> term id)
         - idf: hash map (term id -> normalised frequency of that term)
         - TFIDF: hash map (document id -> array of tf-idf weight of that document (each weight maps to a term id; e.g. arr[42] = tf-idf of term id 42))
         - L2Norm: hash map (document id -> L2 norm of that document)

         ===Local variables===
         TFIDFSearcher
         - doc_id: int (keep track of current document id)
         - term_id: int (keep track of current term id)
         - setTokens: hash set (set that has unique tokens in a document)
         - df: hash map (term id -> frequency of that term)
         - listTokens: linked list (list of all tokens in a document)
         - tf: double (holds calculated term frequency for each term in a document)
         - temp: double (holds sum of square of tf-idf weight in each document)
         - tfidf_weight: array of double (holds tf-idf weight in a document (each weight maps to a term id; e.g. arr[42] = tf-idf of term id 42))

         Search
         - result: linked list (list of SearchResult (Document and the score))
         - tokenised: list (list of tokens from the query)
         - query_tf: double (normalised term frequency of a term)
         - temp: SearchResult (SearchResult for adding to the list)
         - q_weight: array of double (array of query's tf-idf weight where each array stores term id)
         - cur_doc_weight: array of double (current document's array of tf-idf weight)
         - q_l2norm: double (l2 norm of query)
         - cosine_sim: double (cosine similarity of the document and the query)
         - checkNan: int (to check if there are a lot of NaN score)
         ************************************************/

        int doc_id = 0, term_id = 0;
        HashSet<String> setTokens;
        HashMap<Integer, Integer> df = new HashMap<>();
        ArrayList<String> listTokens;
        double tf = 0, temp;
        double[] tfidf_weight;

        for (Document d : documents) {
            if (!docDict.containsKey(d)) {
                docDict.put(d, doc_id);
                doc_id++;
            }
            for (String term : d.getTokens()) {
                if (!termDict.containsKey(term)) {
                    termDict.put(term, term_id);
                    term_id++;
                }
            }
        }

        for (Document d : documents) {
            setTokens = new HashSet<>();
            setTokens.addAll(d.getTokens());

            for (String t : setTokens) {
                if (!df.containsKey(termDict.get(t))) df.put(termDict.get(t), 1);
                else df.replace(termDict.get(t), df.get(termDict.get(t)) + 1);
            }
        }

        for(Integer key: df.keySet()) {
            idf.put(key, (Math.log10(1 + documents.size() / (double) df.get(key))));
        }

        for(Document d: documents) {
            listTokens = new ArrayList<>();
            listTokens.addAll(d.getTokens());
            tfidf_weight = new double[termDict.size()];
            temp = 0;

            for (String t : listTokens) {
                tf = Collections.frequency(listTokens, t);
                tf = tf > 0 ? (1 + Math.log10(tf)) : 0;
                tfidf_weight[termDict.get(t)] = idf.get(termDict.get(t)) * tf;
            }
            for(int i=0; i<tfidf_weight.length; i++) {
                temp += Math.pow(tfidf_weight[i], 2);
            }
            TFIDF.put(docDict.get(d), tfidf_weight);
            L2Norm.put(docDict.get(d), Math.sqrt(temp));
        }
        /***********************************************/
    }

    @Override
    public List<SearchResult> search(String queryString, int k) {
        /************* YOUR CODE HERE ******************/

        LinkedList<SearchResult> result = new LinkedList<>();
        List<String> tokenised = Searcher.tokenize(queryString);
        double query_tf;
        SearchResult temp;
        double[] q_weight = new double[termDict.size()];
        double[] cur_doc_weight;
        double q_l2norm = 0, cosine_sim;
        int checkNan = docDict.size();

        for (String t : tokenised) {
            if (termDict.containsKey(t)) {
                query_tf = Collections.frequency(tokenised, t);
                query_tf = query_tf > 0 ? 1 + Math.log10(query_tf) : 0;
                q_weight[termDict.get(t)] = query_tf * idf.get(termDict.get(t));
                q_l2norm += Math.pow(q_weight[termDict.get(t)], 2);
            }
        }

        q_l2norm = Math.sqrt(q_l2norm);

        for (Document d : docDict.keySet()) {
            cur_doc_weight = TFIDF.get(docDict.get(d));
            cosine_sim = 0;

			for (String t : tokenised) {
				if (termDict.get(t) != null) {
					cosine_sim += cur_doc_weight[termDict.get(t)] * q_weight[termDict.get(t)];
				}
			}

            cosine_sim = cosine_sim / (q_l2norm * L2Norm.get(docDict.get(d)));
            if(Double.isNaN(cosine_sim)) checkNan--;
            temp = new SearchResult(d, cosine_sim);
            result.add(temp);
        }

        if (checkNan >= 10) {
            Collections.sort(result);
        } else {
            Collections.sort(result, new Comparator<SearchResult>() {
                public int compare(SearchResult a, SearchResult b) {
                    return a.getDocument().getId() - b.getDocument().getId();
                }
            });
        }
        return result.subList(0, k);
        /***********************************************/
    }

}