package util;

import interfaces.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;

public class BookParser {
	
	// the number of lines to search for meta-data
	private final int metadataLines = 80;
	
	// we split the book into pages while storing.
	// number of lines in each page
	private final int pageSize = 200; 
	
	// indexes of the searchStrings and the corresponding database keys match 
	private final String[] searchStrings = new String[] { 
			Constants.TITLE,
			Constants.AUTHOR, 
			Constants.RELEASE_DATE, 
			Constants.LANGUAGE,
			Constants.POSTING_DATE, 
			Constants.LAST_UPDATED,
			Constants.TRANSLATOR, 
			Constants.CHARACTER_SET_ENCODING };
	
	private final String[] keys = new String[] { 
			Constants.ATTR_TITLE,
			Constants.ATTR_AUTHOR, 
			Constants.ATTR_RELEASE_DATE,
			Constants.ATTR_LANGUAGE, 
			Constants.ATTR_POSTING_DATE,
			Constants.ATTR_LAST_UPDATED, 
			Constants.ATTR_TRANSLATOR,
			Constants.ATTR_CHARSET_ENCODING };
	
	/**
	 * Given a file, Returns a BasicDBObject ready to be put into the database.
	 */
	public BasicDBObject parseFile(BufferedReader reader) {
		BasicDBObject book = new BasicDBObject();
		String line, value;
		ArrayList<BasicDBObject> content = new ArrayList<BasicDBObject>();
		StringBuilder page = new StringBuilder();
		
		int pos = 0; // position in file
		int pagePos = 0; // position within a page
		int foundAt = 0;
		
		try {
			while((line=reader.readLine()) != null) {
				if (pos < metadataLines) {
					// Extract any meta data present in this line
					for (int i = 0; i < searchStrings.length; i++) {
						if ((foundAt=line.indexOf(searchStrings[i])) != -1) {
							// If value found, then extract it and break
							// TODO more accurate value extraction, dates rather
							// than strings
							value = line.substring(
									foundAt + searchStrings[i].length()).trim();
							book.append(keys[i], value);
						}
					}
				}
				
				if (pagePos < pageSize) {
					// append to page
					page.append(line);
					page.append("\n");
				}
				else {
					// add page to content array-list
					content.add(new BasicDBObject(Constants.ATTR_PAGE, page.toString()));
					// reset page and n
					page = page.delete(0, page.length());
					pagePos = 0;
					// add the current line to the new page
					page.append(line);
					page.append("\n");
				}
				pos++;
				pagePos++;
			}
			// add any remaining page
			content.add(new BasicDBObject(Constants.ATTR_PAGE, page.toString()));
			
			// add the page numbers
			int pageCounter = 1;
			for (BasicDBObject basicDBObject : content) {
				basicDBObject.append(Constants.ATTR_PAGE_NUMBER, pageCounter);
				pageCounter++;
			}
			
			// finally, add the content and pageCount field to the book
			book.append(Constants.ATTR_CONTENT, content);
			book.append(Constants.ATTR_PAGE_COUNT, content.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return book;
	}

}
