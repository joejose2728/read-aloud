package edu.sjsu.readaloud.neo4j;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import edu.sjsu.readaloud.neo4j.util.BookParser;

public class ReadAloudApiTest {

	private static final String TEST_RESOURCES_PATH = "resources/test-data";
	
	private File[] books;
	
	@Before
	public void setUp() throws Exception {
		File inputDir = new File(TEST_RESOURCES_PATH);
		FilenameFilter fnf = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".txt");
			}
		};
		
		books = inputDir.listFiles(fnf);
	}

	@Test
	public void testPopulateGraph() throws URISyntaxException, IOException {
		ReadAloudApi api = new ReadAloudApi();
		for (File book: books){
			api.populateGraph(BookParser.parseFile(book));
		}
	}

}
