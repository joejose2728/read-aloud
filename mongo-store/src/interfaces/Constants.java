package interfaces;

public interface Constants {
	
	// Search strings while loading from file
	String TITLE = "Title:";
	String AUTHOR = "Author:";
	String RELEASE_DATE = "Release Date:";
	String LANGUAGE = "Language:";
	String POSTING_DATE = "Posting Date:";
	String LAST_UPDATED = "Last updated:";
	String TRANSLATOR = "Translator:";
	String CHARACTER_SET_ENCODING = "Character set encoding:";
	
	// Database parameters
	String DB_HOST = "localhost";
	int DB_PORT = 27017;
	String DB_DATABASE = "readaloud";
	
	String COLLECTION_BOOKS = "books";
	String COLLECTION_USERS = "users";
	String COLLECTION_BOOK_CLUBS = "bookClubs";
	
	// Collection: books
	String ATTR_TITLE = "title";
	String ATTR_AUTHOR = "author";
	String ATTR_RELEASE_DATE = "releaseDate";
	String ATTR_LANGUAGE = "language";
	String ATTR_POSTING_DATE = "postingDate";
	String ATTR_LAST_UPDATED = "lastUpdated";
	String ATTR_TRANSLATOR = "translator";
	String ATTR_CHARSET_ENCODING = "characterSetEncoding";
	
	// Only in Normal schema
	String ATTR_CONTENT = "content";
	String ATTR_PAGE = "page";
	String ATTR_PAGE_NUMBER = "pageNumber";
	String ATTR_PAGE_COUNT = "pageCount";
	
	// Only in GridFS
	String ATTR_FILE_NAME = "fileName";
	
	// Common
	String ATTR_UPLOADED_BY = "uploadedBy";
	String ATTR_COMMENTS = "comments";
	String ATTR_USER_ID = "userId";
	String ATTR_COMMENT = "comment";
	
	// Collection: users
	String ATTR_USER_NAME = "userName";
	String ATTR_RECOMMENDATIONS = "recommendations";

	// Collection: bookClubs
	String ATTR_CLUB_NAME = "clubName";
	String ATTR_CLUB_MEMBERS = "clubMembers";
	
	
	// Resources
	String RES_TEST_DATA = "/Users/bdeo/sjsu/dev/cmpe226-project2/read-aloud"
			+ "/mongo-store/resources/test-data";
	

}
