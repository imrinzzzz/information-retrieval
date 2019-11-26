/* Pada     Kanchanapinpong 6088079 Sec 1
 * Thanirin Trironnarith    6088122 Sec 1
 * Wipu     Kumthong        6088095 Sec 1
 */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class BasicIndex implements BaseIndex 
{
        //private static final int INT_BYTES = Integer.SIZE / Byte.SIZE;
	@Override
	public PostingList readPosting(FileChannel fc) {
		/*
		 * TODO: Your code here
		 *       Read and return the postings list from the given file.
		 */
		PostingList postlist= null;
		int a = 0;
			try {
				//first read for termid and list length
				ByteBuffer buf = ByteBuffer.allocate(4);
				a=fc.read(buf);
				if (a == -1)
					{
					return null;
					}
				
				buf.rewind();
				int length = buf.getInt();
				buf.clear();
				postlist = new PostingList(length);
				fc.read(buf);
				//buf = ByteBuffer.allocate(4*length);
				//read list inside
				if (a == -1) 
				{
					return null;
				}
				buf.rewind();
				length = buf.getInt();
				buf.clear();
				for (int i=0; i < length; i++)
				{
					 a = fc.read(buf);//read all file that are left
                     buf.rewind();
                     int length2 = buf.getInt();
                     postlist.getList().add(length2); //add into postlist
                     buf.clear();
					
				}
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return postlist;
	}
	@Override	
	public void writePosting(FileChannel fc, PostingList p)
        {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		ArrayList<Integer> arr = new ArrayList<>();
		ByteBuffer buff = ByteBuffer.allocate(4*(p.getList().size()+2)); 
		arr.add(p.getTermId());
		arr.add(p.getList().size());
		//System.out.print(arr);
		//System.out.println(p.getList());
		for (int doc : p.getList())
		{
			arr.add(doc); //add into arr
		}
		Integer[] a = new Integer[arr.size()]; 
		a = arr.toArray(a); //re-insert with proper sequence
		for (Integer in : a) 
        {
					//System.out.println(a);
                    buff.putInt(in); //put int to bytebuff
        }
		//System.out.println(count*p2);
		buff.flip(); 
		try 
		{
		//	System.out.print(buff);
			fc.write(buff); //write out
			 buff.clear();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}


