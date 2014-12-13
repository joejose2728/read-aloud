package store;

import java.io.BufferedReader;
import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;

import interfaces.IDataLoader;

public class FileLoaderGridFS implements IDataLoader{
	
	
	Mongo mongo;
	DB db;

	public void init(){
		try {
			mongo = new Mongo("localhost", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = mongo.getDB("booksDb");
	}
	
	@Override
	public void loadBooksFromFiles(BufferedReader[] readers, boolean metadata) {
		// TODO Auto-generated method stub
		
		
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException {
		
	}

}
