package app;

import interfaces.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import store.FileDataLoader;

public class MultipleFileLoaderApp {
	
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
		
		BufferedReader readers[] = new BufferedReader[files.length];
		
		try {
			for (int i = 0; i < readers.length; i++) {
				readers[i] = new BufferedReader(new FileReader(
						Constants.RES_TEST_DATA + "/" + files[i]));
				System.out.println(Constants.RES_TEST_DATA + "/" + files[i]);
			}
			
			// pass the readers pointing to all the files in the directory to
			// the data loader
			fdl.loadBooksFromFiles(readers);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (readers != null) {
				try {
					for (int j = 0; j < readers.length; j++)
						readers[j].close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
