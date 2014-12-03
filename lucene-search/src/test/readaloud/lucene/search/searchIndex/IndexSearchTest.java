package test.readaloud.lucene.search.searchIndex;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import readaloud.lucene.search.searchIndex.IndexSearch;

public class IndexSearchTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testSearchinIndexedFiles() throws ParseException, IOException {
		String arg1 = "A KNIGHT OF THE CUMBERLAND";
		String arg2 = "Project"; 
		String arg1ANDarg2 = arg1+"AND"+arg2;
		
		IndexSearch is = new IndexSearch();
		long st = System.currentTimeMillis();
		
		int files_searched = is.searchIndexedFiles(arg1); 
		System.out.println("Searching finished..."); 
		System.out.println("Search returned "+ files_searched +" results" + " for word " +arg2+" in "+(System.currentTimeMillis() - st)+"ms");

	}

}
