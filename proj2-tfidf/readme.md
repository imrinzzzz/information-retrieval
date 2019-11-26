# Term Frequency and Inverted Document Frequency (TF-IDF)

In this project, we use 2 algorithms; which are TF-IDF and Jaccard. TF-IDF is an algorithm that uses indexing and gives a score to each document based on meticulous calculation while Jaccard, on the other hand, simply uses a hit-or-miss method.

## Q&As

1. Which search algorithm (Jaccard vs. TF-IDF) is a better search algorithm for the LISA corpus, in terms of relevance and time consumption? Quantitatively justify your reason scientifically and statistically (i.e. avoid using your gut feelings).

![Comparing two searchers](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/compare-relevance-searchers.png)
![Jaccard-based document searcher](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/jaccard-time.png)
![TFIDF-based document searcher](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/tfidf-time.png)

According to the output of TF-IDF and Jaccard programs, TF-IDF is a better search algorithm than Jaccard for the LISA corpus in terms of relevance, but in terms of time consumption, Jaccard has slightly better performance. 

This is due to the nature of searching in both algorithms. Since there are complicated and massive calculations in order to weight the document in TF-IDF, it yields better relevance with a price of slightly slower time consumption. 

2. Currently, k is fixed at 10. Compute the average precision, recall, F1 for both the search systems for each k (i.e. precision@k, recall@k, and F1@k), where k ranges from 1...50. (You should write a script that automatically does this for you, instead of manually changing k.) Visualize your findings on beautiful and illustrative plots. What conclusions can you make?

![precision score](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/precision.png)
![recall score](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/recall.png)
![f1 score](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/f1-score.png)

From the graphs, it shows the relationship between the different k-value (y-axis) and the result of precision/recall/f1 score (x-axis). In every k-value, the search result from TF-IDF algorithm always has higher scores (precision/recall/f1) compared to Jaccard algorithm. While my TF-IDF algorithm (from myCoolSearcher) yields a slightly better result than TF-IDF algorithm and much better than the Jaccard algorithm.

For Example, If k is fixed at 10, 

The precision of TF-IDF is approximately 0.24 or lower, but the precision of Jaccard is approximately 0.1172, while my TF-IDF is approximately 0.2457 or higher.

    The recall of TF-IDF is approximately 0.3265, but the precision of Jaccard is approximately 0.1296, while my TF-IDF is approximately 0.3333.

    The f1 score of tf-iff is around 0.2267, but the f1 score of Jaccard is only around 0.9837, while my TF-IDF is around 0.2310.

If we compare the result from Jaccard, TF-IDF and myCoolSearcher(my TF-IDF). myCoolSearcher has the result score better than Jaccard and slightly better than result from TF-IDF in some range. 

3. From 2.), generate precision vs recall plots for each search system. Explain how you can use these plots to explain the performance of each search algorithm.

![precision-recall curve](https://raw.githubusercontent.com/imrinzzzz/information-retrieval/master/proj2-tfidf/images/precision-recall-curve.png)

The precision-recall graph is the comparison between recall and precision using different algorithms which are Jaccard, TF-IDF, and my TF-IDF (myCoolSearcher). For Jaccard algorithm, the higher the precision, the lower the recall and vice versa. This also happens with TF-IDF and my TF-IDF, but isn’t as obvious. The graph shows that TF-IDF algorithm always has a higher score than the Jaccard algorithm. Additionally, my TF-IDF generally has a slightly better score than the TF-IDF algorithm.
