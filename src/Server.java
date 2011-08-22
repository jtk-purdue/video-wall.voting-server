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
	String password;
	ConnectionManager connections;
	
	Server(boolean isActive, String password) {
		list = new ListManager(false);
		brodcast = new Brodcaster();
		n = new NecManager(isActive);
		this.password = password;
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
		
		connections = new ConnectionManager();
		
		v = new voteThread(list, brodcast, connections);
		v.start();
		
		
		/*
		 * Set up socket and loop through waiting for connections
		 */
		try{
			ss = new ServerSocket(4242);
			System.out.println("Waiting for connection");
			
			while(true){
				socket = ss.accept();
				Connection connection = new Connection(socket, ss, list, v, brodcast,n,password, connections);
				connections.add(connection);
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
		//java Server pwd=<PASSWORD> active=<TRUE/FALSE>
		Server server;
		String password="boiler";
		String s;
		String s1;
		String s2;
		boolean mode = false;
		int loc;
		for(int i=0; i < args.length; i++){
			s = args[i];
			if(s.contains("=")){
				loc = s.indexOf('=');
				s1=s.substring(0, loc);
				s2=s.substring(loc+1);
				if(s1.equals("password")){
					password = s2;
					System.out.println("password is: "+s2);
				}else if(s1.equals("active")){
					if(s2.equals("TRUE")){
						mode = true;
					}
					
					System.out.println("mode is"+ s2);
				}
			}
		}
		
		
		server = new Server(mode, password);
		server.run();
	}

}
