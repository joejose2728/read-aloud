package readaloud.lucene.search.addFilesToIndex;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import readaloud.lucene.search.intf.Constants;
import readaloud.lucene.search.util.MyFileReader;

public class IndexCreator implements Constants {
	
	
	public int createIndex(String FILES_TO_INDEX_DIRECTORY) throws IOException{
		int number_of_files_index = 0;
		Analyzer analyzer = new StandardAnalyzer();
		boolean recreateIndexIfExists = true;
		IndexWriter indexWriter = new IndexWriter(INDEX_DIRECTORY, analyzer, recreateIndexIfExists);
	
		MyFileReader fr = new MyFileReader(FILES_TO_INDEX_DIRECTORY);
		File[] files = fr.getFileForReading();
		
		for (File file : files) {
			
			Document document = new Document();
			String path = file.getCanonicalPath();
			document.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.UN_TOKENIZED));
			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_CONTENTS, reader));

			indexWriter.addDocument(document); 
			System.out.println("Indexing file: "+file.getName()); 
			number_of_files_index++;
		}
		indexWriter.optimize();
		indexWriter.close();
	return number_of_files_index;
	}
}
