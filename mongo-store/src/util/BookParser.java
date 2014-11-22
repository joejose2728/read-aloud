package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;

public class BookParser {
	
	private final int metadataLines = 80;
	
	// we split the book into segments while storing.
	// number of lines in each segment
	private final int segmentSize = 200; 
	
	// indexes of the searchStrings and the corresponding database keys match 
	private final String[] searchStrings = new String[] { "Title:", "Author:",
			"Release Date:", "Language:", "Posting Date:", "Last updated:",
			"Translator:", "Character set encoding:" };
	private final String[] keys = new String[] { "title", "author",
			"releaseDate", "language", "postingDate", "lastUpdated",
			"translator", "characterSetEncoding", "text", "segments" };
	
	/**
	 * Given a file, Returns a BasicDBObject ready to be put into the database.
	 */
	public BasicDBObject parseFile(BufferedReader reader) {
		BasicDBObject book = new BasicDBObject();
		String line, value;
		ArrayList<String> text = new ArrayList<String>();
		String segment = "";
		
		int n = 0;
		int foundAt = 0;
		
		try {
			while((line=reader.readLine()) != null) {
				n++;
				if (n < metadataLines) {
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
				
				if (n < segmentSize) {
					// append to segment
					segment = segment + line + "\n";
				}
				else {
					// add segment to text array-list
					text.add(segment);
					// reset segment and n
					segment = "";
					n = 0;
				}
			}
			// add any remaining segment
			text.add(segment);
			
			// finally, add the text and segments field to the book
			book.append("text", text.toArray());
			book.append("segments", text.size());
			
			text.clear();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return book;
	}

}
