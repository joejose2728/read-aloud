package store;

import interfaces.Constants;
import interfaces.IQuery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import model.Book;

import org.bson.types.ObjectId;

import util.BookParser;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class ReadAloudStore implements IQuery{
	
	private final String userName;
	private ObjectId userId;
	
	private final String host;
	private final int port;
	
	private MongoClient mongoClient;
	private DB db;
	private DBCollection coll;
	
	public ReadAloudStore(String userName, String host, int port){
		this.userName = userName;
		this.host = host;
		this.port = port;
		init();
	}
	
	private void init() {
		try {
			mongoClient = new MongoClient(host, port);
			
			// Init the store with its user
			db = mongoClient.getDB(Constants.DB_DATABASE);
			coll = db.getCollection(Constants.COLLECTION_USERS);
			
			// See:
			// http://docs.mongodb.org/manual/reference/method/db.collection.update/
			// http://docs.mongodb.org/manual/reference/operator/update/setOnInsert/#up._S_setOnInsert
			// Only if new userName, insert it
			// If userName exists, do nothing to the existing user document
			BasicDBObject userName = new BasicDBObject(Constants.ATTR_USER_NAME, this.userName);
			BasicDBObject userNameUpdate = new BasicDBObject("$setOnInsert", userName);
			WriteResult wr = coll.update(userName,		// match query
						userNameUpdate, // update only if new userName
						true, 			// upsert = true
						false);			// multi = false (anyways, there wont be multiple matches)
			
			// Get and store the user's _id
			if(wr.getN() == 0) userId = (ObjectId)userName.get( "_id" );
			else userId = (ObjectId)wr.getUpsertedId();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void putBook(String bookAbsoluteFilePath, String title, String author) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(bookAbsoluteFilePath));
			BookParser parser = new BookParser();
			BasicDBObject bdo = parser.parseFile(reader, false);
			bdo.append(Constants.ATTR_TITLE, title);
			bdo.append(Constants.ATTR_AUTHOR, author);
			bdo.append(Constants.ATTR_UPLOADED_BY, userId);
			coll.insert(bdo);
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Put book operation failed.");
		}
	}

	@Override
	public Book getBook(String title) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		Book b = new Book();
		BasicDBObject criteria = new BasicDBObject(Constants.ATTR_TITLE, title);
		// Excluding values
		BasicDBObject projection = new BasicDBObject(Constants.ATTR_UPLOADED_BY, 0);
		projection.append(Constants.ATTR_COMMENTS, 0);
		
		try{
			DBCursor c = coll.find(criteria, projection);
			if(c.hasNext()) b = createBook(c.next(), true);
			else throw new RuntimeException();
		} catch(RuntimeException r){
			System.out.println("No such book found. Get book query failed.");
		}
		
		return b;
	}

	@Override
	public Book getBookWithPageNumber(String title, int pageNumber, boolean metadata) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		Book b = new Book();
		// Building query to get specific page from the content
		// db.books.find({"title":"Peter Pan","content.pageNumber":34},{"content.$":1})
		BasicDBObject criteria = new BasicDBObject(Constants.ATTR_TITLE, title);
		criteria.append(Constants.ATTR_CONTENT + ".pageNumber", pageNumber);
		BasicDBObject projection = new BasicDBObject(Constants.ATTR_CONTENT + ".$", 1);
		projection.append(Constants.ATTR_TITLE, 1);
		projection.append(Constants.ATTR_AUTHOR, 1);
		projection.append(Constants.ATTR_RELEASE_DATE, 1);
		projection.append(Constants.ATTR_LANGUAGE, 1);
		projection.append(Constants.ATTR_POSTING_DATE, 1);
		projection.append(Constants.ATTR_LAST_UPDATED, 1);
		projection.append(Constants.ATTR_TRANSLATOR, 1);
		projection.append(Constants.ATTR_CHARSET_ENCODING, 1);
		
		try{
			DBCursor c = coll.find(criteria, projection);
			if(c.hasNext()) b = createBook(c.next(), metadata);
			else throw new RuntimeException();
		} catch(RuntimeException r){
			System.out.println("No such book found, or page number is invalid"
					+ "Get book with page number query failed.");
		}
		
		return b;
	}
	
	private Book createBook(DBObject o, boolean metadata) {
		Book b = new Book();
		String s = new String();
		StringBuilder sb = new StringBuilder();
		
		// Add metadata if required
		if(metadata) {
			if((s=(String) o.get(Constants.ATTR_TITLE)) != null) b.setTitle(s);
			if((s=(String) o.get(Constants.ATTR_AUTHOR)) != null) b.setAuthor(s);
			if((s=(String) o.get(Constants.ATTR_RELEASE_DATE)) != null) b.setReleaseDate(s);
			if((s=(String) o.get(Constants.ATTR_LANGUAGE)) != null) b.setLanguage(s);
			if((s=(String) o.get(Constants.ATTR_POSTING_DATE)) != null) b.setPostingDate(s);
			if((s=(String) o.get(Constants.ATTR_LAST_UPDATED)) != null) b.setLastUpdated(s);
			if((s=(String) o.get(Constants.ATTR_TRANSLATOR)) != null) b.setTranslator(s);
			if((s=(String) o.get(Constants.ATTR_CHARSET_ENCODING)) != null) b.setCharacterSetEncoding(s);
		}
		
		BasicDBList content = (BasicDBList) o.get(Constants.ATTR_CONTENT);
		for (Iterator<Object> it = content.iterator(); it.hasNext();) {
			BasicDBObject bdo = (BasicDBObject) it.next();
			sb.append(bdo.get(Constants.ATTR_PAGE));
		}
		b.setContent(sb.toString());
		sb.delete(0,sb.length());
		
		return b;
	}

	@Override
	public void addCommentToBook(String title, String comment) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		BasicDBObject newComment = new BasicDBObject(Constants.ATTR_USER_ID, userId);
		StringBuilder sb = new StringBuilder(Calendar.MONTH);
		sb.append(" ").append(Calendar.DAY_OF_MONTH).append(", ").append(Calendar.YEAR);
		newComment.append(Constants.ATTR_POSTING_DATE, sb.toString());
		newComment.append(Constants.ATTR_COMMENT, comment);
		
		// example: 
		// db.books.update({title:"Moby Dick"},{$push:{"comments":{"postingDate":"28 July, 2013","comment":"good book"}}})
		BasicDBObject query = new BasicDBObject(Constants.ATTR_TITLE, title);
		BasicDBObject update = new BasicDBObject("$push", new BasicDBObject(Constants.ATTR_COMMENTS, newComment));
		try {
			WriteResult wr = coll.update(query, update);
			if(wr.getN() == 0) throw new RuntimeException();
		} catch (RuntimeException r) {
			System.out.println("Book does not exist. Add comment to book failed.");
		}
	}

	@Override
	public ArrayList<String> getCommentsOnBook(String title) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		ArrayList<String> comments = new ArrayList<String>();
		BasicDBObject criteria = new BasicDBObject(Constants.ATTR_TITLE, title);
		BasicDBObject projection = new BasicDBObject(Constants.ATTR_COMMENTS, 1);
		
		DBCursor c = coll.find(criteria, projection);
		while(c.hasNext()) {
			BasicDBList commentsList = (BasicDBList) c.next().get(Constants.ATTR_COMMENTS);
			for (Iterator<Object> it = commentsList.iterator(); it.hasNext();) {
				BasicDBObject bdo = (BasicDBObject) it.next();
				comments.add(bdo.getString(Constants.ATTR_COMMENT));
			}
		}
		if (comments.size() != 0) return comments;
		else throw new RuntimeException("Book has no comments, or book not found. Get comments on book operation failed");
	}

	@Override
	public void addBookClub(String clubName) {
		coll = db.getCollection(Constants.COLLECTION_BOOK_CLUBS);
		BasicDBObject bdo = new BasicDBObject(Constants.ATTR_CLUB_NAME, clubName);
		coll.insert(bdo);
	}

	@Override
	public void joinBookClub(String clubName) {
		coll = db.getCollection(Constants.COLLECTION_BOOK_CLUBS);
		
		// example: db.bookClubs.update({"clubName":"Fantasy World"},{$push:{"clubMembers":userId}})
		BasicDBObject query = new BasicDBObject(Constants.ATTR_CLUB_NAME, clubName);
		BasicDBObject update = new BasicDBObject("$push", new BasicDBObject(Constants.ATTR_CLUB_MEMBERS, userId));
		try {
			WriteResult wr = coll.update(query, update);
			if(wr.getN() == 0) throw new RuntimeException();
		} catch (RuntimeException r) {
			System.out.println("Book club does not exist. Join book club operation failed");
		}
	}

	@Override
	public ArrayList<String> search(String query) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		ArrayList<String> results = new ArrayList<String>();
		// example: db.books.find({$text:{$search:"England"}},{"title":1});
		BasicDBObject criteria = new BasicDBObject("$text", new BasicDBObject("$search", query));
		BasicDBObject projection = new BasicDBObject(Constants.ATTR_TITLE, 1);
		DBCursor c = coll.find(criteria, projection);
		BasicDBObject bdo = new BasicDBObject();
		while(c.hasNext()) {
			bdo = (BasicDBObject) c.next();
			results.add(bdo.getString(Constants.ATTR_TITLE));
		}
		
		if (results.size() == 0) throw new RuntimeException("Search query matched no books.");
		else return results;
	}
}
