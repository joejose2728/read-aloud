package model;

import org.bson.types.ObjectId;

public class BookGridFS {
	private String title;
	private String author;
	private String releaseDate;
	private String language;
	private String postingDate;
	private String lastUpdated;
	private String translator;
	private String characterSetEncoding;
	private ObjectId fileRef;
	private String content;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getTranslator() {
		return translator;
	}
	public void setTranslator(String translator) {
		this.translator = translator;
	}
	public String getCharacterSetEncoding() {
		return characterSetEncoding;
	}
	public void setCharacterSetEncoding(String characterSetEncoding) {
		this.characterSetEncoding = characterSetEncoding;
	}
	public ObjectId getFileRef() {
		return fileRef;
	}
	public void setFileRef(ObjectId fileRef) {
		this.fileRef = fileRef;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Title: " + title + "\n");
		sb.append("Author: " + author + "\n");
		sb.append("\n\n----------Content fetched from GridFS------------------\n\n ");
		sb.append(content);
		return sb.toString();
	}

}
