package interfaces;

import java.util.ArrayList;

import model.Book;

public interface IQuery {
	
	/* Books */
	// Allow an user to load a single book file. Provide an absolute file path
	public void putBook(String bookAbsoluteFilePath, String title, String author);
	public Book getBook(String title);
	// Get a particular page from the book. metadata flag determines whether
	// the book's metadata is returned
	public Book getBookWithPageNumber(String title, int pageNumber, boolean metadata);
	
	/* Comments */
	public void addCommentToBook(String title, String comment);
	public ArrayList<String> getCommentsOnBook(String title);
	
	/* Book Clubs */
	public void addBookClub(String clubName);
	public void joinBookClub(String clubName);
	
	/* Search */
	// Returns titles of books having the query string in their content
	public ArrayList<String> search(String query);
}
