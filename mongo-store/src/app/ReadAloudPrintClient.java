package app;

import java.io.PrintWriter;
import java.util.Scanner;

import store.ReadAloudStore;

public class ReadAloudPrintClient {
	
	public static void main(String[] args) {
		String bookAbsoluteFilePath, title, author, comment, clubName, query, userName, host;
		int queryNumber, pageNumber, port;
		boolean metadata;
		PrintWriter pw = new PrintWriter(System.out);
		
		// Connect to DB
		Scanner s = new Scanner(System.in);
		System.out.print("\nConnection details: \n");
		System.out.print("userName: ");
		userName = s.nextLine();
		System.out.print("host(localhost): ");
		host = s.nextLine();
		System.out.print("port(27017): ");
		port = s.nextInt();
		s.nextLine(); // skip \n not consumed by nextInt
		
		ReadAloudStore ras = new ReadAloudStore(userName, host, port);
		System.out.println("Logged in to Readaloud Social Literacy Cloud as: " + userName + "\n");
		
		// Perform queries
		try{
			while(true) {
				printMenu();
				System.out.println("Enter query number: ");
				queryNumber = s.nextInt();
				s.nextLine();
				switch (queryNumber) {
				case 1:
					System.out.print("Put Book: \n");
					System.out.print("Book's Absolute file path (.txt): ");
					bookAbsoluteFilePath = s.nextLine();
					System.out.print("Title: ");
					title = s.nextLine();
					System.out.print("Author: ");
					author = s.nextLine();
					ras.putBook(bookAbsoluteFilePath, title, author);
					System.out.print("\n DONE \n");
					break;
				
				case 2:
					System.out.print("Get Book: \n");
					System.out.print("Enter Title: ");
					title = s.nextLine();
					pw.print(ras.getBook(title).toString());
					pw.flush();
					System.out.print("\n DONE \n");
					break;
					
				case 3:
					System.out.print("Get Book with page number: \n");
					System.out.print("Enter Title: ");
					title = s.nextLine();
					System.out.print("Enter Page Number: ");
					pageNumber = s.nextInt();
					s.nextLine();
					System.out.print("Need book metadata? (true/false): ");
					metadata = s.nextBoolean();
					s.nextLine();
					pw.print(ras.getBookWithPageNumber(title, pageNumber, metadata));
					pw.flush();
					System.out.print("\n DONE \n");
					break;
					
				case 4:
					System.out.print("Add comment to book: \n");
					System.out.print("Enter Title: ");
					title = s.nextLine();
					System.out.print("Enter Comment: ");
					comment = s.nextLine();
					ras.addCommentToBook(title, comment);
					System.out.print("\n DONE \n");
					break;
					
				case 5:
					System.out.print("Get comments on book: \n");
					System.out.print("Enter Title: ");
					title = s.nextLine();
					System.out.print(ras.getCommentsOnBook(title));
					System.out.print("\n DONE \n");
					break;
					
				case 6:
					System.out.print("Add a book club: \n");
					System.out.print("Enter Club Name: ");
					clubName = s.nextLine();
					ras.addBookClub(clubName);
					System.out.print("\n DONE \n");
					break;
					
				case 7:
					System.out.print("Join a book club: \n");
					System.out.print("Enter Club Name: ");
					clubName = s.nextLine();
					ras.joinBookClub(clubName);
					System.out.print("\n DONE \n");
					break;
					
				case 8:
					System.out.print("Search for books containing: \n");
					System.out.print("Enter query: ");
					query = s.nextLine();
					System.out.print(ras.search(query));
					System.out.print("\n DONE \n");
					break;
					
				default:
					System.out.print("\n Invalid query number \n");
					break;
				}
			}
		} finally {
			s.close();
			pw.close();
		}
	}
	
	public static void printMenu() {
		System.out.print("Readaloud Social Literacy Cloud: \n");
		System.out.print("-------------------------------- \n");
		System.out.print("USAGE: \n");
		System.out.print("1. putBook\n");
		System.out.print("2. getBook\n");
		System.out.print("3. getBookWithPageNumber\n");
		System.out.print("4. addCommentToBook\n");
		System.out.print("5. getCommentsOnBook\n");
		System.out.print("6. addBookClub\n");
		System.out.print("7. joinBookClub\n");
		System.out.print("8. search\n");
		System.out.print("-------------------------------- \n");
	}

}
