package app;

import interfaces.Constants;

import java.io.File;
import java.io.FilenameFilter;

import store.FileDataLoader;

/*
 * To load Project Gutenberg books into GridFS implementation
 */
public class MultipleFileLoaderGridFSApp {
	public static void main(String[] args)
	{
		FileDataLoader fdl = new FileDataLoader();
		File inD = new File(Constants.RES_TEST_DATA);
		
		String[] files = inD.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		
		// Adding full path
		for (int i = 0; i < files.length; i++) {
			files[i] = Constants.RES_TEST_DATA + '/' + files[i];
		}
		fdl.loadBooksFromFilesIntoGridFS(files, true);
	}
}
