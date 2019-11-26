
/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Index {

	// Term id -> (position in index file, doc frequency) dictionary
	private static Map<Integer, Pair<Long, Integer>> postingDict 
		= new TreeMap<Integer, Pair<Long, Integer>>();
	// Doc name -> doc id dictionary
	private static Map<String, Integer> docDict
		= new TreeMap<String, Integer>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict
		= new TreeMap<String, Integer>();
	// Block queue
	private static LinkedList<File> blockQueue
		= new LinkedList<File>();

	// Total file counter
	private static int totalFileCount = 0;
	// Document counter
	private static int docIdCounter = 0;
	// Term counter
	private static int wordIdCounter = 0;
	// Index
	private static BaseIndex index = null;

	
	/* 
	 * Write a posting list to the given file 
	 * You should record the file position of this posting list
	 * so that you can read it back during retrieval
	 * 
	 * */
	private static void writePosting(FileChannel fc, PostingList posting)
			throws IOException {
			
		/*
		 * TODO: Your code here
		 *	 
		 */
		int size = posting.getList().size();
		long pos = fc.position();
		Pair<Long,Integer> pair = new Pair<Long,Integer>(pos,size);
		postingDict.put(posting.getTermId(),pair);
		//System.out.println(postingDict);
		index.writePosting(fc, posting);
	}
	

	 /**
     * Pop next element if there is one, otherwise return null
     * @param iter an iterator that contains integers
     * @return next element or null
     */
    private static Integer popNextOrNull(Iterator<Integer> iter) {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return null;
        }
    }
	
    
   
	
	/**
	 * Main method to start the indexing process.
	 * @param method		:Indexing method. "Basic" by default, but extra credit will be given for those
	 * 			who can implement variable byte (VB) or Gamma index compression algorithm
	 * @param dataDirname	:relative path to the dataset root directory. E.g. "./datasets/small"
	 * @param outputDirname	:relative path to the output directory to store index. You must not assume
	 * 			that this directory exist. If it does, you must clear out the content before indexing.
	 */
	public static int runIndexer(String method, String dataDirname, String outputDirname) throws IOException 
	{
		/* Get index */
		String className = method + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}
		
		/* Get root directory */
		File rootdir = new File(dataDirname);
		if (!rootdir.exists() || !rootdir.isDirectory()) {
			System.err.println("Invalid data directory: " + dataDirname);
			return -1;
		}
		
		   
		/* Get output directory*/
		File outdir = new File(outputDirname);
		if (outdir.exists() && !outdir.isDirectory()) {
			System.err.println("Invalid output directory: " + outputDirname);
			return -1;
		}
		
		for (File file: outdir.listFiles()) //finding all file and listout
		{ 
			if (!file.isDirectory()) //if file is inside 
			{
		        file.delete(); //delete
			}
		}
		
		/*TODO: delete all the files/sub folder under outdir
		 * 
		 */
		if (!outdir.exists()) {
			if (!outdir.mkdirs()) {
				System.err.println("Create output directory failure");
				return -1;
			}
		}
		
		
		
		
		/* BSBI indexing algorithm */
		File[] dirlist = rootdir.listFiles();

		/* For each block */
		for (File block : dirlist) {
			File blockFile = new File(outputDirname, block.getName());
			//System.out.println("Processing block "+block.getName());
			blockQueue.add(blockFile);

			File blockDir = new File(dataDirname, block.getName());
			File[] filelist = blockDir.listFiles();
			TreeMap<Integer, TreeSet<Integer> > blockMap = new TreeMap<Integer, TreeSet<Integer>>();
			/* For each file */
			for (File file : filelist) {
				++totalFileCount;
				String fileName = block.getName() + "/" + file.getName();
				
				 // use pre-increment to ensure docID > 0
                int docId = ++docIdCounter;
                docDict.put(fileName, docId);
				
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					String[] tokens = line.trim().split("\\s+");
					for (String token : tokens) {
						/*
						 * TODO: Your code here
						 *       For each term, build up a list of
						 *       documents in which the term occurs
						 */
						//System.out.println(termDict);
						if(termDict.containsKey(token)!= true) //if this term aren't in termdict  
						{
							wordIdCounter = wordIdCounter + 1; //add wordcount
							termDict.put(token, wordIdCounter); //put token with wordcount in termdict
						}
						if(blockMap.containsKey(termDict.get(token))!= true) //if it not in blockmap
						{
							TreeSet<Integer> t = new TreeSet<Integer>(); //create tree set
							blockMap.put(termDict.get(token),t); //put in blockmap with new treeset
						}
						blockMap.get(termDict.get(token)).add(docDict.get(fileName));
					}
				}
				//System.out.println(termDict);
				//System.out.println(blockMap);
				reader.close();
			}

			/* Sort and output */
			if (!blockFile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}
			
			RandomAccessFile bfc = new RandomAccessFile(blockFile, "rw");
			FileChannel filechannel = bfc.getChannel();
			/*
			 * TODO: Your code here
			 *       Write all posting lists for all terms to file (bfc) 
			 */
			for (int termId : blockMap.keySet()) //loop according to termid inside blockmap
			{
				List<Integer> list = new ArrayList<Integer>(blockMap.get(termId)); //get termid from blockmap to list
				PostingList post = new PostingList(termId,list); //create posting list with termid and list of it as linked list
				//System.out.println(list);
				index.writePosting(filechannel,post); //write out
			}
			bfc.close();
			
		}

		/* Required: output total number of files. */
		System.out.println("Total Files Indexed: "+totalFileCount);

		/* Merge blocks */
		while (true) {
			if (blockQueue.size() <= 1)
				break;

			File b1 = blockQueue.removeFirst();
			File b2 = blockQueue.removeFirst();
			
			File combfile = new File(outputDirname, b1.getName() + "+" + b2.getName());
			if (!combfile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}

			RandomAccessFile bf1 = new RandomAccessFile(b1, "r");
			RandomAccessFile bf2 = new RandomAccessFile(b2, "r");
			RandomAccessFile mf = new RandomAccessFile(combfile, "rw");

			FileChannel bfc1 = bf1.getChannel(); //open file channel for 3 file
			FileChannel bfc2 = bf2.getChannel();
			FileChannel mfc = mf.getChannel();

			
			/*
			 * TODO: Your code here
			 *       Combine blocks bf1 and bf2 into our combined file, mf
			 *       You will want to consider in what order to merge
			 *       the two blocks (based on term ID, perhaps?).
			 *       
			 */
			  long pos =0;
              PostingList p = index.readPosting(bfc1.position(0)); //keep posting list of file bf1
              Map<Integer,PostingList> merge = new TreeMap<>(); //to keep merge file
              while(p !=null) 
              {
                  merge.put(p.getTermId(),p); //put postinglist into merge map
                  pos= pos + ( 8+(4*p.getList().size())); //finding postion
                  p = index.readPosting(bfc1.position(pos)); //keep into postinglist p
              }
              pos = 0;
              p = index.readPosting(bfc2.position(0)); //redo for bfc2
              while(p != null) //if p contain postinglist inside or not
              {
                  if(merge.containsKey(p.getTermId()))  //if in map merge there are this termid
                  {
                      for(int i: p.getList()){  //search inside for each termid that also doesn't inside merge map
                          if(merge.get(p.getTermId()).getList().contains(i) == false)
                          {
                              merge.get(p.getTermId()).getList().add(i);// add termid that not contain in map 
                          }
                      }          
                  }
                  else 
                  {
                      merge.put(p.getTermId(),p); //replaced the termid
                  }
              pos = pos + (8+(4*p.getList().size())); //update for the next position 
              p = index.readPosting(bfc2.position(pos));
              }
              for(int i: merge.keySet())
              {
                  Collections.sort(merge.get(i).getList()); //sort the list in merge
                  writePosting(mfc, merge.get(i)); //write out
              }
			bf1.close();
			bf2.close();
			mf.close();
			b1.delete();
			b2.delete();
			blockQueue.add(combfile);
		}
		/* Dump constructed index back into file system */
		File indexFile = blockQueue.removeFirst();
		indexFile.renameTo(new File(outputDirname, "corpus.index"));

		BufferedWriter termWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "term.dict")));
		for (String term : termDict.keySet()) {
			termWriter.write(term + "\t" + termDict.get(term) + "\n");
		}
		termWriter.close();

		BufferedWriter docWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "doc.dict")));
		for (String doc : docDict.keySet()) {
			docWriter.write(doc + "\t" + docDict.get(doc) + "\n");
		}
		docWriter.close();

		BufferedWriter postWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "posting.dict")));
		for (Integer termId : postingDict.keySet()) {
			postWriter.write(termId + "\t" + postingDict.get(termId).getFirst()
					+ "\t" + postingDict.get(termId).getSecond() + "\n");
		}
		postWriter.close();
		
		return totalFileCount;
	}

	public static void main(String[] args) throws IOException {
		/* Parse command line */
		if (args.length != 3) {
			System.err
					.println("Usage: java Index [Basic|VB|Gamma] data_dir output_dir");
			return;
		}

		/* Get index */
		String className = "";
		try {
			className = args[0];
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get root directory */
		String root = args[1];
		

		/* Get output directory */
		String output = args[2];
		runIndexer(className, root, output);
	}

}
