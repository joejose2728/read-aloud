package test.readaloud.lucene.search.addFilesToIndex;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import readaloud.lucene.search.addFilesToIndex.IndexCreator;

public class IndexCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Make sure file and dir exists 
		//
	}

	@Test
	public void testCreateIndex() { 
			
			//Step-1: create a index of all text files you have 
			String path_to_files= "C:\\Users\\Gaurav\\Gaurav_DATA\\EST226\\ReadOutLoud\\ReadOutLoud\\filesToIndex"; 
							
		  try {
				IndexCreator ic = new IndexCreator();
				long st = System.currentTimeMillis();
				System.out.println("Indexing Started at: "+st);
			    int number_of_files = ic.createIndex(path_to_files); //1
				System.out.println("Indexing Done"); 
				System.out.println("Total time taken to index "+number_of_files+" files: "+ (System.currentTimeMillis() - st)+"ms");
							
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
}
