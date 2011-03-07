import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

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