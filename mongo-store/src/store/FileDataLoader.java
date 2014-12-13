package store;

import interfaces.Constants;
import interfaces.IDataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import util.BookParser;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class FileDataLoader implements IDataLoader{
	
	private MongoClient mongoClient;	
	private DB db;
	private DBCollection coll;
	private BulkWriteOperation bwo;
	private GridFS gfs;
	
	public FileDataLoader() {
		init();
	}
	
	private void init() {
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
	public void loadBooksFromFiles(BufferedReader[] readers, boolean metadata) {
		db = mongoClient.getDB(Constants.DB_DATABASE);
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		bwo = coll.initializeUnorderedBulkOperation();
		
		int batchSize = 10;
		int n;
		BasicDBObject[] books = new BasicDBObject[batchSize];
		BookParser parser = new BookParser();
		
		for (int i = 0; i < readers.length; i++) {
			// reusing fixed size array of objects
			n = i % batchSize;
			books[n] = parser.parseFile(readers[i], metadata);
			bwo.insert(books[n]); // adding to batch
			
			if (n == batchSize - 1) {
				bwo.execute(); // execute bulk write once batch is ready
			}
		}
		bwo.execute(); // any remaining operations
		close();
	}
	
	@Override
	public void loadBooksFromFilesIntoGridFS(String[] filePaths, boolean metadata) {
		db = mongoClient.getDB(Constants.DB_DATABASE);
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		gfs = new GridFS(db);
		
		BookParser parser = new BookParser();
		ObjectId fileRef = new ObjectId();
		for (int i = 0; i < filePaths.length; i++) {
			try {
				GridFSInputFile in = gfs.createFile(new File(filePaths[i]));
				in.setChunkSize(16 * 1024); // 16KB chunk size
				in.setContentType("text/plain");
				in.save();
				fileRef = (ObjectId)in.getId();
				BufferedReader reader = new BufferedReader(new FileReader(filePaths[i]));
				BasicDBObject bdo = new BasicDBObject();
				if(metadata) bdo = parser.parseOnlyFileMetadata(reader);
				bdo.append(Constants.ATTR_FILE_REF, fileRef);
				coll.insert(bdo);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		close();
	}

	@Override
	public void close() {
		mongoClient.close();
	}
	
}
