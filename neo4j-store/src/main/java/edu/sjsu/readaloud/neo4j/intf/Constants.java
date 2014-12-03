package edu.sjsu.readaloud.neo4j.intf;

public interface Constants {

	public String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	
	// Search strings while loading from file
		String TITLE = "Title:";
		String AUTHOR = "Author:";
		String RELEASE_DATE = "Release Date:";
		String LANGUAGE = "Language:";
		String POSTING_DATE = "Posting Date:";
		String LAST_UPDATED = "Last updated:";
		String TRANSLATOR = "Translator:";
		String CHARACTER_SET_ENCODING = "Character set encoding:";
		
		//book attributes
		String ATTR_TITLE = "title";
		String ATTR_AUTHOR = "author";
		String ATTR_RELEASE_DATE = "releaseDate";
		String ATTR_LANGUAGE = "language";
		String ATTR_POSTING_DATE = "postingDate";
		String ATTR_LAST_UPDATED = "lastUpdated";
		String ATTR_TRANSLATOR = "translator";
		String ATTR_CHARSET_ENCODING = "characterSetEncoding";
		
		//user attributes
		String ATTR_USER_NAME = "name";
		
		//page attributes
		String ATTR_DATA = "data";
}
