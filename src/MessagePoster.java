import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class MessagePoster {
	private static final int CAPACITY = 5;
	private static final int MAXCHARS = 120;
	
	private static final String ADDRESS = "pc.cs.purdue.edu";
	private static final String RSSHEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n<rss version=\"2.0\">\n<channel>\n\t<title>Lawson Videowall Posts</t><link>"+ADDRESS+"</link>\n\t<description>Posts to the Lawson Videowall at Purdue</description>";
	private static final String RSSEND = "</channel></rss>";
	
	Set<String> badWords;
	List<Post> posts;
	
	String fullOutput;
	
	MessagePoster(){
		posts = new ArrayList<Post>();
		badWords = new HashSet<String>();
		try {
			Scanner s = new Scanner(new File("badwords.txt"));
			while(s.hasNext()){
				badWords.add(s.nextLine());
			}
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
	
	boolean isProfane(String str){
		boolean profane = false;
		Scanner s = new Scanner(str);
		while(s.hasNext() && !profane){
			if(badWords.contains(s.next()))
				profane = true;
		}
		return profane;
	}
	
	void submit(String name, String message){
		if(!isProfane(name) && !isProfane(message) && message.length() <= MAXCHARS){
			Post p = new Post(name, message, ADDRESS);
			add(p);
		}
	}
	
	synchronized void add(Post p){
		if(posts.size() == CAPACITY)
			posts.remove(CAPACITY - 1);
		posts.add(0, p);
		fullOutput = RSSHEADER;
		for(int i=0; i < CAPACITY; i++){
			fullOutput+=posts.get(i).rssEntry;
		}
		fullOutput+=RSSEND;
		//TODO: write to file
	}
	
	private class Post{
		String name;
		String message;
		String rssEntry;
		long timestamp;

		public Post(String name, String message,String address){
			this.name = name;
			this.message = message;
			this.rssEntry = "\t<item>\n\t\t<title>"+this.name+"</title>\n\t\t<link>"+address+"</link>\n\t\t<description>"+this.name+" -- "+this.message+"</description>\n\t</item>";
		}
	}
}


