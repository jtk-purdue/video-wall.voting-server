import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class UserThread extends Thread {
	
	Socket connection;
	ServerSocket ss;
	PrintWriter out;
	BufferedReader in;
	ThreadGroup children;
	Date lastChanged;
	ListManager list;
	Brodcaster brodcast;
	
	//will need to add global elements from Server class as they are added
	UserThread(Socket connection, ServerSocket ss, Date update,ListManager list, Brodcaster brodcast){
		this.connection = connection;
		this.ss = ss;
		this.lastChanged = update;
		this.list=list;
		this.brodcast = brodcast;
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
		System.out.println("check");
		int i;		
		if(message[0].equals("GET")){
			for(i = 0; i < (list.getListSize()); i++)
			{
				if(list.get(i) != null)
					sendMessage(list.get(i).name);
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
			sendMessage("END");
			list.vote(message[1]);
		} 
		else if(message[0].equals("GETLIST")){
			for(i = 0; i < (list.getListSize()); i++)
			{
				if(list.get(i) != null)
					sendMessage(list.get(i).name + ": " + list.get(i).vote);
			}
			sendMessage("END");
		}
		else if(message[0].equals("TRIGGER")){
			try{
				sendMessage("END");
				brodcast.sendAll(message[2]);
			}catch(Exception e){}
		}
		else if(message[0].equals("ONETRIG")){
			try{
				sendMessage("END");
				URL url = new URL(message[1]);
				brodcast.sendOne(url,message[2]);
			}catch(Exception e){}
		}
		else if(message[0].equals("GETCOUNT")){
			alphaNumbers();
			sendMessage("END");
		}
		else {
			sendMessage("END");
		}
		
			
	}
	
	void alphaNumbers()
	{
		for(int i = 0; i < list.getListSize(); i++)
		{
			sendMessage(list.getAlpha(i));
		}
		
	}
}
