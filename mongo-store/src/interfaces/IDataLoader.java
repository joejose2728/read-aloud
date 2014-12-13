package interfaces;

import java.io.BufferedReader;

public interface IDataLoader {
	
	// Set metadata flag to true, if book to be loaded is in standard project gutenberg format
	// If a user wants to upload their own file, then metadata flag should be set to false
	public void loadBooksFromFiles(BufferedReader[] readers, boolean metadata);
	
	// To load files into GridFS. Input list of absolute file paths
	public void loadBooksFromFilesIntoGridFS(String[] filePaths, boolean metadata);
	
	public void close();
	
}
