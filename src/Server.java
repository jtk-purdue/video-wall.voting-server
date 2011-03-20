import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
public class Server{
	ServerSocket providerSocket;
	Socket connection = null;
	Date lastChanged = new Date();
	ListManager list;
	Brodcaster brodcast;
	voteThread voteT;
	VoteItem voteList[];
	Server() {
		list = new ListManager();
		brodcast = new Brodcaster();
	}
	void run()
	{		
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
		
		voteList = new VoteItem[list.getListSize()];
		for(int i = 0; i < list.getListSize(); i++)
		{
			voteList[i] = list.get(i);
		}
		
		voteT = new voteThread(list, brodcast);
		voteT.start();
		try{
			providerSocket = new ServerSocket(4242);
			System.out.println("Waiting for connection");
			
			while(true){
				connection = providerSocket.accept();
				UserThread usr = new UserThread(connection, providerSocket, lastChanged,list, brodcast);
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
