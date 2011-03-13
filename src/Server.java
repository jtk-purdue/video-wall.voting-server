import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
public class Server{
	ServerSocket providerSocket;
	Socket connection = null;
	Date lastChanged = new Date();
	ListManager list;
	
	//should also include anything that needs to be centralized (vote counts etc.)
	//need another thread to maintain half-life decay of votes when something is being voted on
	VoteItem shows[] = new VoteItem[20];

	
	Server() {
		list = new ListManager();
	}
	void run()
	{
		//add initial channels to the line up
		list.add("Discovery");
		list.add("Lifetime");
		list.add("Fox News");
		list.add("Comedy Central");
		list.add("CNN");
		list.add("History");
		list.add("ESPN");
		list.add("ESPN 2");
		list.add("Big Ten Network");
		list.add("Spike");
		list.add("WTHR 13");
		list.add("WISHTV 8");
		
		shows[0] = new VoteItem("Simpsons");
		shows[1] = new VoteItem("Family Guy");
		shows[2] = new VoteItem("Scrubs");
		shows[3] = new VoteItem("How I Met Your Mother");
		shows[4] = new VoteItem("Big Bang Theory");
		shows[5] = new VoteItem("Chuck");
		shows[6] = new VoteItem("NCIS");
		shows[7] = new VoteItem("Smallville");
		shows[8] = new VoteItem("Two and a Half Men");
		shows[9] = new VoteItem("Lost");
		shows[10] = new VoteItem("24");
		shows[11] = new VoteItem("Justice League");
		shows[12] = new VoteItem("Green Lantern");
		shows[13] = new VoteItem("Friends");
		shows[14] = new VoteItem("Seinfeld");
		shows[15] = new VoteItem("Modern Family");
		// shows[0] = new VoteItem("Choice 1");
		// shows[1] = new VoteItem("Choice 2");
		// shows[2] = new VoteItem("Choice 3");
		// shows[3] = new VoteItem("Choice 4");
		// shows[4] = new VoteItem("Choice 5");
		// shows[5] = new VoteItem("Choice 6");
		// shows[6] = new VoteItem("Choice 7");
		// shows[7] = new VoteItem("Choice 8");
		// shows[8] = new VoteItem("Choice 9");
		// shows[9] = new VoteItem("Choice 10");
		// shows[10] = new VoteItem("Choice 11");
		// shows[11] = new VoteItem("Choice 12");
		// shows[12] = new VoteItem("Choice 13");
		// shows[13] = new VoteItem("Choice 14");
		// shows[14] = new VoteItem("Choice 15");
		// shows[15] = new VoteItem("Choice 16");
		

		try{
			providerSocket = new ServerSocket(4242);
			System.out.println("Waiting for connection");
			
			while(true){
				connection = providerSocket.accept();
				UserThread usr = new UserThread(connection, providerSocket, lastChanged,list);
				usr.start();
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Server server = new Server();
		while(true){
			server.run();
		}
	}

}
