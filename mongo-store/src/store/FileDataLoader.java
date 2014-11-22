package store;

import interfaces.Constants;
import interfaces.IDataLoader;

import java.io.BufferedReader;
import java.net.UnknownHostException;

import util.BookParser;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class FileDataLoader implements IDataLoader{
	
	private MongoClient mongoClient;	
	private DB db;
	private DBCollection coll;
	private BulkWriteOperation bwo;
	
	public FileDataLoader() {
		init();
	}
	
	public void init() {
		try {
			// TODO connect to DB here to check early if its up, rather than
			// finding out after loading the files
			mongoClient = new MongoClient(Constants.DB_HOST, Constants.DB_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		// Commented out, as this is the default write concern level
		// Write operations that use this write concern will wait for
		// acknowledgement from the primary server before returning. Exceptions
		// are raised for network issues, and server errors.
		
		// mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
	}

	@Override
	public void loadBooksFromFiles(BufferedReader[] readers) {
		db = mongoClient.getDB(Constants.DB_DATABASE);
		coll = db.getCollection(Constants.DB_COLLECTION);
		bwo = coll.initializeUnorderedBulkOperation();
		
		int batchSize = 10;
		int n;
		BasicDBObject[] books = new BasicDBObject[batchSize];
		BookParser parser = new BookParser();
		
		for (int i = 0; i < readers.length; i++) {
			// reusing fixed size array of objects
			n = i % batchSize;
			books[n] = parser.parseFile(readers[i]);
			bwo.insert(books[n]); // adding to batch
			
			if (n == batchSize - 1) {
				bwo.execute(); // execute bulk write once batch is ready
			}
		}
		bwo.execute(); // any remaining operations
		close();
	}

	@Override
	public void close() {
		mongoClient.close();
	}
	

}
