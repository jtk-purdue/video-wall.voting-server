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
		
	Set<String> badWords;
	List<Post> posts;
	
	String filepath;
	String fullOutput;
	boolean isActive;
	
	MessagePoster(String filepath, boolean isActive){
		this.filepath = filepath;
		this.isActive = isActive;
		String cuss;
		posts = new ArrayList<Post>();
		badWords = new HashSet<String>();
		try {
			Scanner s = new Scanner(new File("badwords.txt"));
			while(s.hasNext()){
				cuss = s.next();
				if(cuss.endsWith("*"))
					cuss = cuss.substring(0, cuss.length()-1);
				badWords.add(cuss);
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
			Post p = new Post(name, message, System.currentTimeMillis());
			System.out.println("adding");
			add(p);
		}
	}
	
	public synchronized void add(Post p){
		if(posts.size() == CAPACITY)
			posts.remove(CAPACITY - 1);
		posts.add(0, p);
		
		fullOutput = "";
		for(int i=0; i< posts.size(); i++){
			fullOutput += posts.get(i).name+": "+posts.get(i).message+"\n";
		}
		if(isActive){
			File f = new File(filepath);
			try {
				FileWriter out = new FileWriter(f);
				out.write(fullOutput);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Post{
		String name;
		String message;
		String date;
		long timestamp;

		public Post(String name, String message,long timestamp){
			this.name = name;
			this.message = message;
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


