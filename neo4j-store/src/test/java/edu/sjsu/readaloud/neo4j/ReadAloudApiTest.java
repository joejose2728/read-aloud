package edu.sjsu.readaloud.neo4j;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.sjsu.readaloud.neo4j.util.BookParser;
import edu.sjsu.readaloud.neo4j.util.Neo4JUtil;

public class ReadAloudApiTest {

	private static final String TEST_RESOURCES_PATH = "resources/test-data";

	private File[] books;
	private ReadAloudApi api;

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
		api = new ReadAloudApi();
	}

	@Test
	@Ignore
	public void testPopulateGraph() throws URISyntaxException, IOException {

		for (File book: books){
			api.populateGraph(BookParser.parseFile(book));
		}
	}

	@Test
	@Ignore
	public void testGetPage3OfAllBooks(){
		String query = "MATCH (b:Book)-[:CONTAINS {pageNum: 3}]->(p:Page) RETURN b,p";
		Neo4JUtil.sendCypherQuery(query ,"{}");
	}

	@Test
	@Ignore
	public void testAddComment() throws URISyntaxException{
		URI bookUri = new URI("http://localhost:7474/db/data/node/437");
		api.addComment(bookUri, "This book is awesome. Must read!");

		bookUri = new URI("http://localhost:7474/db/data/node/487");
		api.addComment(bookUri, "The Time Machine is a revelation");
	}

	@Test
	@Ignore
	public void testViewComment(){
		String query = "MATCH (b:Book{title:'The Time Machine'})-[:HAS]->(c) RETURN c";
		Neo4JUtil.sendCypherQuery(query ,"{}");
	}

	@Ignore
	@Test
	public void testGetBooksAndTheirPages(){
		int limit = 10000;
		String query = "MATCH (b:Book)-[:CONTAINS]->(p:Page) RETURN b,p";
		for (int i=0; i<limit;i++){
			Neo4JUtil.sendCypherQuery(query ,"{}");
		}
	}
	
	@Test
	public void testGetAuthorsOfTheBook(){
		int limit = 10000;
		String query = "MATCH (b:Book {title: 'The Time Machine'})-[:AUTHORED]->(a:Author) RETURN a";
		for (int i=0; i<limit;i++){
			Neo4JUtil.sendCypherQuery(query ,"{}");
		}
	}
	
	@Test
	public void testGetCommentsOnTheBook(){
		int limit = 10000;
		String query = "MATCH (b:Book {title: 'The Time Machine'})-[:HAS]->(c:Comment) RETURN c";
		for (int i=0; i<limit;i++){
			Neo4JUtil.sendCypherQuery(query ,"{}");
		}
	}
}
