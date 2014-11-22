package interfaces;

import java.io.BufferedReader;

public interface IDataLoader {

	public void loadBooksFromFiles(BufferedReader[] readers);
	public void close();
	
}
