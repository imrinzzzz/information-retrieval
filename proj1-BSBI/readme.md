# Blocked sort-based indexing (BSBI)

## Brief Description

First of all, we have 2 main tasks which are indexing and querying. Before we can query anything we have to finish indexing the files first.

### Indexing

When we try **testIndex** in P1Tester, it will call method **runIndexer** from the class **Index**. The method will first process directory (code given by the instructor), then it will create **termDict**, **docDict**, and **blockDict** which are all Tree Maps according to the BSBI algorithm. The algorithm is simple, working on a block and a file in that block at a time, then use if/else to check whether we already have recorded this document (file) and/or term or not. If we have, then skip it, but if we don’t just count the term using **worIdCounter** and add it to the Tree Map(s) created. Whenever the term is added, it will also be added in **blockMap** which stores *term ID* and a set (more specifically, a TreeSet) of *doc ID*.Basically, if the term is not yet in **blockMap**, we will store the term along with the newly created set which will store the *docID*. If there already is the term, we will just add *docID* to the set using *termID* to identify.

After every file in a block is processed, we will create a **postingList** (An object given by the instructor which contains *termID* and a list of *docID*) called post by calling method **writePosting** from the class **BasicIndex**. Basically, **writePosting** just writes the **postingList** to the destination we specify. 


After every block is processed, we will merge 2 **postingList** files at a time until there is only one left using **readPosting** method from the class **BasicIndex** to provide postingList information from the byte position.

### Querying

After finishing the index of files, we can now run **testQuery** method in P1Tester. It will first create a **Query** object from a method called *queryService* and then call **runQueryService** method from the class **Query**. The method will read term dictionary, doc dictionary, and posting dictionary (code provided). 

After that, the **testQuery** method will call another method in the class **Query** called **retrieve**. It will find the query word(s) in postingList using method called **readPosting** (again, from the class **BasicIndex**). If we find the match in the postingList, we will add it to the arrayList we create called list. After we complete filling up the list, we will check for duplicates which will return another arrayList but without duplication.  

## Efficiency of the algorithms
<table>
<tr>
  <td rowspan="2"> </td>
  <th colspan="2">Index</th>
  <th colspan="2">Query</th>
<tr>
<tr>
  <td></td>
  <th>Memory used (MB)</th>
  <th>Time used (sec)</th>
  <th>Memory used (MB)</th>
  <th>Time used (sec)</th>
<tr>
<tr>
  <th>Small</th>
  <td>0.754888</td>
  <td>0.155</td>
  <td>0.301808</td>
  <td>0.035</td>
<tr>
<tr>
  <th>Large</th>
  <td>584.064296</td>
  <td>861.614</td>
  <td>151.072448</td>
  <td>3.51</td>
<tr>
<tr>
  <th>Citeseer</th>
  <td>440.996616</td>
  <td>773.952</td>
  <td>36.110848</td>
  <td> 1.162</td>
<tr>
</table>
