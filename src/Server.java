import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
public class Server{
	ServerSocket ss;
	Socket socket = null;
	ListManager list;
	Broadcaster brodcast;
	voteThread v;
	NecManager n;
	String password;
	ConnectionManager connections;
	int secondsToWait;
	boolean isActive;
	GlobalVars global;
	MessagePoster messages;
	String filePath;
	
	Server(boolean isActive, String password, int secondsToWait, String filePath) {
		list = new ListManager(false);
		brodcast = new Broadcaster();
		n = new NecManager(isActive);
		this.isActive = isActive;
		this.password = password;
		this.secondsToWait = secondsToWait;
		this.filePath = filePath;
	}
	void run()
	{		
//		list.add("CNN","1","cnn");
//		list.add("ESPN","2","espn");
//		list.add("ABC","3","abc");
//		list.add("Nickelodeon","4", "nickelodeon");
//		list.add("Comedy Central","5", "comedyCentral");
//		list.add("Sci Fi","6", "SciFi");
//		list.add("Weather Channel", "7", "weather");
//		list.add("National Geographic", "8","nationalGeographic");
		
		list.add("CS Camp", "1", "cscamp");
		list.add("Double Take", "2", "dblTake");
		list.add("Hot Seat", "3", "hotSeat");
		list.add("Jason Lestina", "4", "jason");
		list.add("Mixable", "5", "mixable");
		
		connections = new ConnectionManager();
		messages = new MessagePoster(filePath, isActive);
		
		v = new voteThread();
		global = new GlobalVars(ss, list, brodcast, v, n, password, connections, secondsToWait, isActive, messages);
		v.setGlobal(global);
		v.start();
		
		
		/*
		 * Set up socket and loop through waiting for connections
		 */
			try{
				ss = new ServerSocket(4242);
				ss.setReuseAddress(true);
				System.out.println("Waiting for connection");
				while(true){
					socket = ss.accept();
					System.out.println(Calendar.getInstance().getTime() +":"+socket.getInetAddress()+"--"+ "Connection initiated: Total connections = "+(((Thread.activeCount() - 2)/2)+1));
					Connection connection = new Connection(socket, global);
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
		String fileLoc = "lawsonmsg.txt";
		String s;
		String s1;
		String s2;
		boolean mode = false;
		int loc;
		int secondsToWait = 0;
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
					if(s2.equalsIgnoreCase("true")){
						mode = true;
					}
					
					System.out.println("mode is "+ mode);
				}else if(s1.equals("wait")){
					try{
						secondsToWait = Integer.parseInt(s2);
					} catch (Exception e){
						System.out.println("poorly formed wait command");
					}
				}else if(s1.equals("mfile")){
					try{
						fileLoc = s2;
					} catch (Exception e){
						System.out.println("poorly formed mfile command");
					}
					System.out.println("message file path is: "+ fileLoc);
				}
			}
		}
		
		
		server = new Server(mode, password, secondsToWait, fileLoc);
		server.run();
//		System.out.println("should never get here");
	}

}
