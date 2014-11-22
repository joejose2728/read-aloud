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
	String DB_COLLECTION = "books";
	
	// Resources
	String RES_TEST_DATA = "/Users/bdeo/sjsu/dev/cmpe226-project2/read-aloud"
			+ "/mongo-store/resources/test-data";
	

}
