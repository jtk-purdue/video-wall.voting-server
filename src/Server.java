import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server{
	ServerSocket providerSocket;
	Socket connection = null;
	ListManager list;
	Brodcaster brodcast;
	voteThread voteT;
	UpdateTracker lastUpdate;
	
	Server() {
		list = new ListManager(false);
		brodcast = new Brodcaster();
		lastUpdate = new UpdateTracker();
	}
	void run()
	{		
		list.add("CNN","1");
		list.add("ESPN","2");
		list.add("ABC","3");
		list.add("Nickelodeon","4");
		list.add("Comedy Central","5");
		list.add("Sci Fi","6");
		list.add("Weather Channel", "7");
		list.add("National Geographic", "8");
		
		
		
		voteT = new voteThread(list, brodcast, lastUpdate);
		voteT.start();
		
		
		/*
		 * Set up socket and loop through waiting for connections
		 */
		try{
			providerSocket = new ServerSocket(4242);
			System.out.println("Waiting for connection");
			
			while(true){
				connection = providerSocket.accept();
				UserThread usr = new UserThread(connection, providerSocket,list, brodcast, voteT, lastUpdate);
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
