import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
	
	String filepath;
	String fullOutput;
	boolean isActive;
	
	MessagePoster(String filepath, boolean isActive){
		this.filepath = filepath;
		this.isActive = isActive;
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
	
	void submit(String name, String message, long timestamp){
		if(!isProfane(name) && !isProfane(message) && message.length() <= MAXCHARS){
			Post p = new Post(name, message, ADDRESS, System.currentTimeMillis());
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
		if(isActive){
			File f = new File(filepath);
			try {
				FileWriter out = new FileWriter(f);
				out.write(fullOutput);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Post{
		String name;
		String message;
		String rssEntry;
		String date;
		long timestamp;

		public Post(String name, String message,String address,long timestamp){
			this.name = name;
			this.message = message;
			this.rssEntry = "\t<item>\n\t\t<title>"+this.name+"</title>\n\t\t<link>"+address+"</link>\n\t\t<description>"+this.name+" -- "+this.message+"</description>\n\t</item>";
			this.timestamp = timestamp;
			
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(timestamp);
			date = c.get(Calendar.DAY_OF_WEEK)+" "+getMonth(c.get(Calendar.MONTH))+" "+c.get(Calendar.DATE)+" "+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((message == null) ? 0 : message.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Post other = (Post) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (timestamp != other.timestamp)
				return false;
			return true;
		}

		private MessagePoster getOuterType() {
			return MessagePoster.this;
		}
		
		private String getMonth(int i){
			String s[] = {"Jan.","Feb.","Mar.","Apr.", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec"};
			return s[i];
		}
	}
}


