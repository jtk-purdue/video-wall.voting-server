import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
public class Server{
	ServerSocket providerSocket;
	Socket connection = null;
	Date lastChanged = new Date();
	
	//should also include anything that needs to be centralized (vote counts etc.)
	//need another thread to maintain half-life decay of votes when something is being voted on
	VoteItem shows[] = new VoteItem[20];

	
	Server() {}
	void run()
	{
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
				UserThread usr = new UserThread(connection, providerSocket, shows, lastChanged);
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

class UserThread extends Thread {
	
	Socket connection;
	ServerSocket ss;
	PrintWriter out;
	BufferedReader in;
	VoteItem ballot[];
	ThreadGroup children;
	Date lastChanged;
	
	//will need to add global elements from Server class as they are added
	UserThread(Socket connection, ServerSocket ss, VoteItem ballot[], Date update){
		this.connection = connection;
		this.ss = ss;
		this.ballot = ballot;
		this.lastChanged = update;
	}
	
	public void run(){
		System.out.println("Connection received from " + connection.getInetAddress().toString());
		try{
		out = new PrintWriter(connection.getOutputStream(), true);
		out.flush();
			
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		sendMessage("Connection successful");
		}catch(Exception e){}
		/****Will Use children potentially when there is more interaction with manager*****/
		//children = new ThreadGroup(this.getName + "_children");
		
		String message[] = new String[10];
		String part = new String();
		int i = 0;
		do{
			try{
				part = in.readLine();
				System.out.println("client>" + part);
				//sendMessage(part);
				message[i] = part;
				i++;
			}catch(Exception e){}
		}while(!part.equals("END"));
		System.out.println("Conection closed from "+ connection.getInetAddress().toString());
		process(message);
		
		/****Will Use children potentially when there is more interaction with manager*****/
		//wait for children threads to finish
		/*while(children.activeCount() > 0){
			yield();
		}*/
		
		try{
				in.close();
				out.close();
				//ss.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		
	}
	
	void sendMessage(String msg)
	{
			out.println(msg);
			out.flush();
			System.out.println("send>" + msg);
	}
	
	public void process(String message[])
	{
		//handle different commands
			//vote, audio, video...
		
		int i;		
		if(message[0].equals("GET")){
			for(i = 0; i < (ballot.length-1); i++)
			{
				if(ballot[i] != null)
					sendMessage(ballot[i].name);
				//System.out.println(ballot[i].name);
			}
			sendMessage("END");
		}
		else if(message[0].equals("CHECK")){
			SimpleDateFormat df = new SimpleDateFormat();
			try{
				Date tmp = df.parse(message[1]);
				if(tmp.before(lastChanged)){
					sendMessage("OLD");
				} else {
					sendMessage("CURRENT");
				}
			}catch(Exception e){}
			sendMessage("END");
		}
		else if(message[0].equals("VOTE")){
			boolean check = false;
			sendMessage("END");
			for(i = 0; i < ballot.length && check == false; i++)
			{
				if(ballot[i] != null){
					if(ballot[i].name.equals(message[1]))
					{
						check = true;
						ballot[i].vote++;
					}
				}
			}
		} 
		else if(message[0].equals("GETCOUNT")){
			for(i = 0; i < (ballot.length); i++)
			{
				if(ballot[i] != null)
					sendMessage(ballot[i].name + ": " + ballot[i].vote);
			}
			sendMessage("END");
		}
		else {
			sendMessage("END");
		}
		
			
	}
}

class VoteItem{
	String name;
	public float vote;
	VoteItem(String n)
	{
		name = n;
		vote = 0;
	}
}
