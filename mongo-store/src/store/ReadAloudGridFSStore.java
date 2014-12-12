package store;

import interfaces.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Iterator;

import model.BookGridFS;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ReadAloudGridFSStore{
	
	private final String userName;
	private ObjectId userId;
	
	private final String host;
	private final int port;
	
	private MongoClient mongoClient;
	private DB db;
	private DBCollection coll;
	private GridFS gfs;
	
	public ReadAloudGridFSStore(String userName, String host, int port) {
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
			gfs = new GridFS(db);
			
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

	public void putBook(String bookAbsoluteFilePath, String title, String author) {
		try {
			GridFSInputFile in = gfs.createFile(new File(bookAbsoluteFilePath));
			in.setChunkSize(16 * 1024); // 16KB chunk size
			in.setContentType("text/plain");
			in.save();
			ObjectId fileRef = (ObjectId)in.getId();
			BasicDBObject bdo = new BasicDBObject(Constants.ATTR_FILE_REF, fileRef);
			bdo.append(Constants.ATTR_TITLE, title).append(Constants.ATTR_AUTHOR, author);
			coll.insert(bdo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BookGridFS getBook(String title) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		BookGridFS b = new BookGridFS();
		// Building query to get the book. Then use the fileRef from the result
		// to get content from GridFS
		// db.books.find({"title":"Peter Pan"});
		BasicDBObject criteria = new BasicDBObject(Constants.ATTR_TITLE, title);
		// Excluding values
		BasicDBObject projection = new BasicDBObject(Constants.ATTR_UPLOADED_BY, 0);
		projection.append(Constants.ATTR_COMMENTS, 0);
		
		try{
			DBCursor c = coll.find(criteria, projection);
			if(c.hasNext()) b = createBookGridFS(c.next(), true);
			else throw new RuntimeException();
			
			GridFSDBFile gfsFile = gfs.find(b.getFileRef());
			BufferedReader br = new BufferedReader(new InputStreamReader(gfsFile.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String s = "";
			try {
				while((s=br.readLine()) != null) {
					sb.append(s + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			b.setContent(sb.toString());
			sb.delete(0, sb.length());
		} catch(RuntimeException r){
			System.out.println("No such book found. Get book query failed.");
		}
		
		return b;
	}

	public BookGridFS getBookWithChunkNumber(String title, int chunkNumber,
			boolean metadata) {
		coll = db.getCollection(Constants.COLLECTION_BOOKS);
		
		BookGridFS b = new BookGridFS();
		// Building query to get the book. Then use the fileRef from the result to get content from GridFS
		// db.books.find({"title":"Peter Pan"});
		BasicDBObject criteria = new BasicDBObject(Constants.ATTR_TITLE, title);
		DBCursor c = coll.find(criteria);
		if(c.hasNext()) b = createBookGridFS(c.next(), metadata);
		
		// Get chunks for the file, sorted by their "n" field
		// db.fs.chunks.find({files_id: ObjectId("5488d3020364e837e0e02444")}, {data:1});
		coll = db.getCollection("fs.chunks");
		criteria = new BasicDBObject("files_id", b.getFileRef());
		DBCursor chunks = coll.find(criteria, new BasicDBObject("data", 1));
		
		// Get the required chunk from the cursor
		BasicDBObject chunk = new BasicDBObject();
		int i = 0;
		while (chunks.hasNext()) {
			chunk = (BasicDBObject) chunks.next();
			if (i == chunkNumber - 1) break;
			else i++;
		}
		
		// Get the base64 encoded binary data from the chunk, decode and set it as the book's content
		String binary = com.mongodb.util.JSONSerializers.getStrict().serialize(chunk.get("data"));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode base64Val;
		try {
			base64Val = mapper.readTree(binary).get("$binary");
			byte[] decoded = Base64.getMimeDecoder().decode(
					base64Val.getTextValue());
			b.setContent(new String(decoded));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	private BookGridFS createBookGridFS(DBObject o, boolean metadata) {
		BookGridFS b = new BookGridFS();
		String s = new String();
		
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
		b.setFileRef((ObjectId) o.get(Constants.ATTR_FILE_REF));
		
		return b;
	}

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

	public void addBookClub(String clubName) {
		coll = db.getCollection(Constants.COLLECTION_BOOK_CLUBS);
		BasicDBObject bdo = new BasicDBObject(Constants.ATTR_CLUB_NAME, clubName);
		coll.insert(bdo);
	}

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

	public ArrayList<String> search(String query) {
		// TODO Auto-generated method stub
		return null;
	}
}
