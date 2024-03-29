
/**/
/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Query {

	// Term id -> position in index file
	private static  Map<Integer, Long> posDict = new TreeMap<Integer, Long>();
	// Term id -> document frequency
	private  Map<Integer, Integer> freqDict = new TreeMap<Integer, Integer>();
	// Doc id -> doc name dictionary
	private  Map<Integer, String> docDict = new TreeMap<Integer, String>();
	// Term -> term id dictionary
	private  Map<String, Integer> termDict = new TreeMap<String, Integer>();
	// Index
	private static  BaseIndex index = null;
	

	//indicate whether the query service is running or not
	private boolean running = false;
	private RandomAccessFile indexFile = null;
	
	/* 
	 * Read a posting list with a given termID from the file 
	 * You should seek to the file position of this specific
	 * posting list and read it back.
	 * */
	private static  PostingList readPosting(FileChannel fc, int termId)
			throws IOException {
		/*
		 * TODO: Your code here
		 */
		//System.out.println(termId);
		//System.out.println(posDict);
		long get = posDict.get(termId); //get position in index file
		//
		
		//System.out.println(get);
		fc.position(get);  //set file channel to get
		return index.readPosting(fc); //pass to readposting to make it read
	}
	
	
	public void runQueryService(String indexMode, String indexDirname) throws IOException
	{
		//Get the index reader
		try {
			Class<?> indexClass = Class.forName(indexMode+"Index");
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}
		
		//Get Index file
		File inputdir = new File(indexDirname);
		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.err.println("Invalid index directory: " + indexDirname);
			return;
		}
		
		/* Index file */
		indexFile = new RandomAccessFile(new File(indexDirname,"corpus.index"), "r");

		String line = null;
		/* Term dictionary */
		BufferedReader termReader = new BufferedReader(new FileReader(new File(indexDirname, "term.dict")));
		while ((line = termReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();

		/* Doc dictionary */
		BufferedReader docReader = new BufferedReader(new FileReader(new File(indexDirname, "doc.dict")));
		while ((line = docReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			docDict.put(Integer.parseInt(tokens[1]), tokens[0]);
		}
		docReader.close();

		/* Posting dictionary */
		BufferedReader postReader = new BufferedReader(new FileReader(new File(indexDirname, "posting.dict")));
		while ((line = postReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			posDict.put(Integer.parseInt(tokens[0]), Long.parseLong(tokens[1]));
			freqDict.put(Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[2]));
		}
		postReader.close();
		this.running = true;
	}
    
	public List<Integer> retrieve(String query) throws IOException
	{	
		if(!running) 
		{
			System.err.println("Error: Query service must be initiated");
		}
		
		/*
		 * TODO: Your code here
		 *       Perform query processing with the inverted index.
		 *       return the list of IDs of the documents that match the query
		 *      
		*/
		List<Integer> p1 = new ArrayList<>();
		List<List<Integer>> list = new ArrayList<>();
		/* For each query */
		//System.out.println(query);
		String[] tokens = query.trim().split(" ");
	
		for(String token: tokens)
			{
				//System.out.println(token);
				//System.out.println(termDict);
				//System.out.println(token);
				
				if(termDict.containsKey(token))
				{
					//System.out.println(termDict.containsKey(token));
					//System.out.println(termDict.get(token));
					list.add(readPosting(indexFile.getChannel(), termDict.get(token)).getList());
					//if termdict contain token add to list
				}
				//System.out.println(list);
			}
				Iterator<List<Integer>> iter = list.iterator();
				List<Integer> l1 = pop(iter);
				List<Integer> l2;
				while (l1 != null && (l2 = pop(iter)) != null)
				{
					List<Integer> resultList = new ArrayList<Integer>();
					Iterator<Integer> i1 = l1.iterator();
					Iterator<Integer> i2 = l2.iterator();
					Integer d1 = pop(i1);
					Integer d2 = pop(i2);
					while (d1 != null && d2 != null) //into loop if d1 and d2 not empty
					{
						if (Objects.equals(d1,d2))
						{
							resultList.add(d1); //if both equal move to next 
							d1 = pop(i1);
							d2 = pop(i2);
						} 
						else if (d1 < d2) //if d2 more than move d1
						{
							d1 = pop(i1);
						} 
						else //last statement d1 more than move d2
						{
							d2 = pop(i2);
						}
					}
					l1 = resultList;
				}
				//System.out.println(l1);
				return l1;
				
			
	}
	private static <X> X pop(Iterator<X> iter) 
    {
		if (iter.hasNext()) 
    	{
			return iter.next();
		} 
		else 
     	{
			return null;
		}
    }	
    String outputQueryResult(List<Integer> res) {
        /*
         * TODO: 
         * 
         * Take the list of documents ID and prepare the search results, sorted by lexicon order. 
         * 
         * E.g.
         * 	0/fine.txt
		 *	0/hello.txt
		 *	1/bye.txt
		 *	2/fine.txt
		 *	2/hello.txt
		 *
		 * If there no matched document, output:
		 * 
		 * no results found
		 * 
         * */
    	StringBuilder str2 = new StringBuilder(""); //create stringbuilder
		if(res == null) //if list is null
        {
			return("NO RESULT FOUND"); //printout there are no result
		}
        for(Integer docId : res) //loop list
        {
        	str2.append(docDict.get(docId)).append("\n"); //append res list 
        }        
        
        //System.out.println(str2.toString());
		return str2.toString();
    	
    	
    }
	
	public static void main(String[] args) throws IOException {
		/* Parse command line */
		if (args.length != 2) {
			System.err.println("Usage: java Query [Basic|VB|Gamma] index_dir");
			return;
		}

		/* Get index */
		String className = null;
		try {
			className = args[0];
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get index directory */
		String input = args[1];
		
		Query queryService = new Query();
		queryService.runQueryService(className, input);
		
		/* Processing queries */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		/* For each query */
		String line = null;
		while ((line = br.readLine()) != null) {
			List<Integer> hitDocs = queryService.retrieve(line);
			queryService.outputQueryResult(hitDocs);
		}
		
		br.close();
	}
	
	protected void finalize()
	{
		try {
			if(indexFile != null)indexFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}