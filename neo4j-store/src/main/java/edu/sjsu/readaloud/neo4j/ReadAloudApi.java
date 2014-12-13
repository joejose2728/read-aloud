package edu.sjsu.readaloud.neo4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import edu.sjsu.readaloud.neo4j.intf.Constants;
import edu.sjsu.readaloud.neo4j.model.Book;
import edu.sjsu.readaloud.neo4j.model.Page;
import edu.sjsu.readaloud.neo4j.model.User;
import edu.sjsu.readaloud.neo4j.util.Neo4JUtil;

public class ReadAloudApi {

	/**
	 * Create book node, author node and page nodes and their relationship.
	 * @param book
	 * @throws URISyntaxException 
	 */
	public void populateGraph(Book book) throws URISyntaxException{
		URI bookUri = Neo4JUtil.createNode();
		Neo4JUtil.addLabel(bookUri, "Book");
		Neo4JUtil.addProperty(bookUri, Constants.ATTR_TITLE, book.getTitle());
		
		//add book properties
		String value = book.getReleaseDate();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_RELEASE_DATE, value);
		
		value = book.getLanguage();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_LANGUAGE, value);
		
		value = book.getPostingDate();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_POSTING_DATE, value);
		
		value = book.getTranslator();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_TRANSLATOR, value);
		
		value = book.getLastUpdated();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_LAST_UPDATED, value);
		
		value = book.getCharacterSetEncoding();
		if (value != null && value.length() > 0)
			Neo4JUtil.addProperty(bookUri, Constants.ATTR_CHARSET_ENCODING, value);
		
		List<User> authors = book.getAuthors();
		for (User author: authors){
			URI authorUri = Neo4JUtil.createNode();
			Neo4JUtil.addLabel(authorUri, "Author");
			Neo4JUtil.addProperty(authorUri, Constants.ATTR_USER_NAME, author.getName());
			Neo4JUtil.createRelationship(bookUri, authorUri, "AUTHORED", "{}");
		}
		
		List<Page> pages = book.getPages();
		int pageNo = 1;
		for (Page page: pages){
			URI pageUri = Neo4JUtil.createNode();
			Neo4JUtil.addLabel(pageUri, "Page");
			Neo4JUtil.addProperty(pageUri, Constants.ATTR_DATA, page.getPageData());
			Neo4JUtil.createRelationship(bookUri, pageUri, "CONTAINS", "{\"pageNum\": " + pageNo++ +" }");
		}
	}
	
	public void addComment(URI bookUri, String comment) throws URISyntaxException{
		URI commentUri = Neo4JUtil.createNode();
		Neo4JUtil.addProperty(commentUri, Constants.ATTR_COMMENT, comment);
		Neo4JUtil.addLabel(commentUri, "Comment");
		Neo4JUtil.createRelationship(bookUri, commentUri, "HAS", "{}");
	}
}
