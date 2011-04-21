import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

class UserThread extends Thread {
	
	Socket connection;
	ServerSocket ss;
	PrintWriter out;
	BufferedReader in;
	ThreadGroup children;
	ListManager list;
	Brodcaster brodcast;
	voteThread v;
	UpdateTracker lastUpdate;
	
	UserThread(Socket connection, ServerSocket ss,ListManager list, Brodcaster brodcast, voteThread v, UpdateTracker lastUpdate){
		this.connection = connection;
		this.ss = ss;
		this.list=list;
		this.brodcast = brodcast;
		this.v = v;
		this.lastUpdate = lastUpdate;
	}
	
	public void run(){
		System.out.println("Connection received from " + connection.getInetAddress().toString());
		
		try{
			out = new PrintWriter(connection.getOutputStream(), true);
			out.flush();
				
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sendMessage("Connection successful");
		}catch(Exception e){}
		
		String message[] = new String[10];
		String part = new String();
		int i = 0;
		do{
			try{
				part = in.readLine();
				System.out.println("client>" + part);
				message[i] = part;
				i++;
			}catch(Exception e){}
		}while(!part.equals("END"));
		System.out.println("Conection closed from "+ connection.getInetAddress().toString());
		process(message);
		
		try{
				in.close();
				out.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		
	}
	
	
	/*
	 * Sends message back to client
	 */
	void sendMessage(String msg)
	{
			out.println(msg);
			out.flush();
			System.out.println("send>" + msg);
	}
	
	
	/*
	 * Parses user input and executes the appropriate action
	 * all responses end with "END"
	 */
	public void process(String message[])
	{
		int i;	
		
		if(message[0].equals("GET")){
			/*
			 * Sends the name of all VoteItems to the user
			 * Note: the list sent is unsorted
			 * Syntax: "GET" "END"
			 */
			for(i = 0; i < (list.getListSize()); i++)
			{
				if(list.get(i) != null)
					sendMessage(list.get(i).name);
			}
			sendMessage("END");
			
		}else if(message[0].equals("VOTE")){
			/*
			 * Increments the vote count of the appropriate VoteItem by 1
			 * Syntax: "VOTE" <name> "END"
			 */
			sendMessage("END");
			list.vote(message[1]);
			synchronized(v){
				v.notify();
			}
		}else if(message[0].equals("GETLIST")){
			/*
			 * sends the list of all names and vote counts
			 * the list is unsorted
			 * Syntax: "GETLIST" "END"
			 */
			for(i = 0; i < (list.getListSize()); i++)
			{
				if(list.get(i) != null)
					sendMessage(list.get(i).name + ": " + list.get(i).vote);
			}
			sendMessage("END");
			
		}else if(message[0].equals("TRIGGER")){
			/*
			 * Sends the specified trigger to all players simultaneously
			 * Syntax: "TRIGGER" <trigger number> "END"
			 */
			try{
				sendMessage("END");
				brodcast.sendAll(message[1]);
			}catch(Exception e){}
			
		}else if(message[0].equals("ONETRIG")){
			/*
			 * Sends the specified trigger to the specified player url
			 * Syntax: "ONETRIG" <player url> <trigger number> "END"
			 */
			try{
				sendMessage("END");
				URL url = new URL(message[1]);
				brodcast.sendOne(url,message[2]);
			}catch(Exception e){}
		}
		else if(message[0].equals("GETCOUNT")){
			/*
			 * returns the number of votes, list is ordered alphabetically by name, but
			 * only votes are sent
			 * Syntax: "GETCOUNT" "END"
			 */
			alphaNumbers();
			sendMessage("END");
		}
		else if(message[0].equals("JCHECKUPDATE")){
			/*
			 * returns "update" the number of votes has changed since time returns "none" otherwise
			 * NOTE: only works with java based systems for now
			 * NOTE: String 'time' should be obtained using the Java Date class and the 'toString' function
			 * Syntax: "JCHECKUPDATE" <time> "END"
			 */
			if(lastUpdate.needUpdate(message[1]))
				sendMessage("update");
			else
				sendMessage("none");
			
			sendMessage("END");
		}
		else if(message[0].equals("NOWPLAYING")){
			/*returns the index of the item with the most votes in an alphabetical list
			 * Syntax "NOWPLAYING" "END"
			 */
			sendMessage(list.nowPlayingIndex()+"");
			sendMessage("END");
		}
		else if(message[0].equals("RESET")){
			sendMessage("END");
			list.clearVotes();
		}
		else {
			//just ignores unrecognized commands
			sendMessage("END");
		}
		
			
	}
	
	void alphaNumbers()
	{
		for(int i = 0; i < list.getListSize(); i++)
		{
			sendMessage(list.getAlphaVote(i));
		}
		
	}
}
