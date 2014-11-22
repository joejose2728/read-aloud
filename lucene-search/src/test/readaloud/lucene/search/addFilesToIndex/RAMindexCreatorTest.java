package test.readaloud.lucene.search.addFilesToIndex;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;
import org.junit.BeforeClass;
import org.junit.Test;

import readaloud.lucene.search.addFilesToIndex.RAMindexCreator;
import readaloud.lucene.search.searchIndex.IndexSearchInRAM;

public class RAMindexCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testCreateIndexInRAM() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException {
		String path_to_files= "C:\\Users\\Gaurav\\Gaurav_DATA\\EST226\\ReadALoud\\ReadALoud\\filesToIndex";
				//change to your path of books directory
		
		RAMindexCreator ric = new RAMindexCreator();
		long st = System.currentTimeMillis();
		int number_of_files = ric.createIndexInRAM(path_to_files);
		
		
		
		System.out.println("Indexing Done"); 
		System.out.println("Total time taken to index "+number_of_files+" files: "+ (System.currentTimeMillis() - st)+"ms"); 
		
		System.out.println("Searching indexes in RAM "); 		
		IndexSearchInRAM isirt = new IndexSearchInRAM();
		String searchString = "Project";
		int number_of_files_searched = isirt.searchIndexInRAM(searchString);
		System.out.println("Found keyword "+searchString+" in "+number_of_files_searched+"documents");
	}

}
