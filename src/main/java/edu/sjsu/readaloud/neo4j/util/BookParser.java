package edu.sjsu.readaloud.neo4j.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.sjsu.readaloud.neo4j.intf.Constants;
import edu.sjsu.readaloud.neo4j.model.Book;
import edu.sjsu.readaloud.neo4j.model.Page;
import edu.sjsu.readaloud.neo4j.model.User;

public class BookParser implements Constants {
	
	// the number of lines to search for meta-data
	private static final int metadataLines = 80;
	
	// we split the book into pages while storing.
	// number of lines in each page
	private static final int pageSize = 200; 
	
	// indexes of the searchStrings and the corresponding database keys match 
	private static final String[] searchStrings = { 
			TITLE,
			AUTHOR, 
			RELEASE_DATE, 
			LANGUAGE,
			POSTING_DATE, 
			LAST_UPDATED,
			TRANSLATOR, 
			CHARACTER_SET_ENCODING };
	
	/**
	 * Given a file, Returns a BasicDBObject ready to be put into the database.
	 * @throws IOException 
	 */
	public static Book parseFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line, value;
		StringBuilder page = new StringBuilder();
		
		int pos = 0; // position in file
		int pagePos = 0; // position within a page
		int foundAt = 0;
		Book book = new Book();
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
							
							switch (searchStrings[i]){
								case TITLE : book.setTitle(value);
									break;
								case LANGUAGE: book.setLanguage(value);
									break;
								case LAST_UPDATED: book.setLastUpdated(value);
									break;
								case POSTING_DATE: book.setPostingDate(value);
									break;
								case RELEASE_DATE: book.setReleaseDate(value);
									break;
								case TRANSLATOR: book.setTranslator(value);
									break;
								case CHARACTER_SET_ENCODING: book.setCharacterSetEncoding(value);
									break;
								case AUTHOR:User user = new User();
											user.setName(value);
											book.addAuthor(user);
							}
							
						}
					}
				}
				
				if (pagePos < pageSize) {
					// append to page
					page.append(line);
					page.append("\\n");
				}
				else {
					// add page to content array-list
					Page pg = new Page();
					pg.setPageData(page.toString());
					book.addPage(pg);
					// reset page and n
					page = page.delete(0, page.length());
					pagePos = 0;
					// add the current line to the new page
					page.append(line);
					page.append("\\n");
				}
				pos++;
				pagePos++;
			}
			// add any remaining page
			Page pg = new Page();
			pg.setPageData(page.toString());
			book.addPage(pg);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			reader.close();
		}
		
		return book;
	}

}