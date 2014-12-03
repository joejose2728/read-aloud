package readaloud.lucene.search.util;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class RamDirectoryHelperClass {

	private static Directory ramDirectory = new RAMDirectory();

	public static Directory getInstance(){
		return ramDirectory;
	}
	
}
