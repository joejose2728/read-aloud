package edu.sjsu.readaloud.neo4j.model;

import java.util.ArrayList;
import java.util.List;

public class Book {

	private String title;
	private String releaseDate;
	private String postingDate;
	private String language;
	private String translator;
	private String characterSetEncoding;
	private String lastUpdated;
	
	private List<Page> pages = new ArrayList<>();
	private List<User> authors = new ArrayList<User>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
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
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	public List<User> getAuthors() {
		return authors;
	}
	public void setAuthors(List<User> authors) {
		this.authors = authors;
	}
	public void addPage(Page pg) {
		pages.add(pg);
	}
	public void addAuthor(User user) {
		authors.add(user);
	}
}
