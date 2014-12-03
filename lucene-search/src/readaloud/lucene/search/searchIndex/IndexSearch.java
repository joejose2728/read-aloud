package readaloud.lucene.search.searchIndex;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import readaloud.lucene.search.intf.Constants;

/**
*@author: Gaurav Bhardwaj  
*This class will get the results for query passed by user out of the indexed results 
*/
public class IndexSearch implements Constants { 
	

	public IndexSearch(){
	//this.searchString = searchString; 	
	}
	
	public int searchIndexedFiles(String searchString) throws ParseException, IOException{ 
		int number_of_files_searched =0;
		Directory directory = FSDirectory.getDirectory(INDEX_DIRECTORY);
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser(FIELD_CONTENTS, analyzer);
		
		Query query = queryParser.parse(searchString);
		Hits hits = indexSearcher.search(query);
		System.out.println("String found in " + hits.length() +" documents"); 
		
		
		Iterator<Hit> it = hits.iterator();
		while (it.hasNext()) { 
			number_of_files_searched++;
			Hit hit = it.next();
			Document document = hit.getDocument();
			String path = document.get(FIELD_PATH);
			System.out.println("Search String found in " + path); // lets print as of now later we can return a array or json value
		}
		return number_of_files_searched;
			
	}
	
	
}
