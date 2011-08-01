import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server{
	ServerSocket ss;
	Socket socket = null;
	ListManager list;
	Brodcaster brodcast;
	voteThread v;
	NecManager n;
	
	Server(boolean isActive) {
		list = new ListManager(false);
		brodcast = new Brodcaster();
		n = new NecManager(isActive);
	}
	void run()
	{		
		list.add("CNN","1","cnn");
		list.add("ESPN","2","espn");
		list.add("ABC","3","abc");
		list.add("Nickelodeon","4", "nickelodeon");
		list.add("Comedy Central","5", "comedyCentral");
		list.add("Sci Fi","6", "SciFi");
		list.add("Weather Channel", "7", "weather");
		list.add("National Geographic", "8","nationalGeographic");
		
		
		
		v = new voteThread(list, brodcast);
		v.start();
		
		
		/*
		 * Set up socket and loop through waiting for connections
		 */
		try{
			ss = new ServerSocket(4242);
			System.out.println("Waiting for connection");
			
			while(true){
				socket = ss.accept();
				Connection connection = new Connection(socket, ss, list, v, brodcast,n);
				
				//UserThread usr = new UserThread(socket, providerSocket,list, brodcast, voteT, lastUpdate);
				//usr.start();
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Server server;
		if(args.length > 0 && args[0].equals("active"))
				server = new Server(true);
		else
				server =  new Server(false);
		server.run();
	}

}
