import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ReadThread extends Thread {
	Socket socket;
	BufferedReader in;
	SendBuffer buf;
	WriteThread w;
	Boolean isConnected;
	Connection connection;
	GlobalVars global;
	boolean isAdmin;
	int numArgs;
	
	ReadThread(Socket socket, GlobalVars global, SendBuffer buf, WriteThread w, Boolean isConnected, Connection connection){
		this.socket = socket;
		this.buf = buf;
		this.w = w;
		this.isConnected = isConnected;
		this.connection = connection;
		this.global = global;
		isAdmin = true;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		String message = new String();
		
		while(isConnected){
			try {
				message = in.readLine();
			} catch (IOException e) {
				System.out.println("End at readThread 58");
				isConnected = false;
				System.out.println(Calendar.getInstance().getTime() +":"+socket.getInetAddress()+"--"+ "Connection terminated by client");
				connection.clear();
				synchronized(w){
					w.isConnected = false;
					w.notify();
				}
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
			
			try{
				if(!message.equals(""))
					process(message);
			}catch(NullPointerException e){
				isConnected = false;
				System.out.println(Calendar.getInstance().getTime() +":"+socket.getInetAddress()+"--"+ "Connection terminated by client");
				synchronized(w){
					w.isConnected = false;
					w.notify();
				}
				connection.clear();
			}
		}
	}
	
	void process(String message){
		System.out.println(Calendar.getInstance().getTime() +":"+socket.getInetAddress()+"--"+ message);
		Scanner s = new Scanner(message);
		String command = s.next();
		if(command.equals("GETLIST")){
			for(int i = 0; i < global.list.getListSize(); i++){
				sendChannel(i);
			}
			synchronized(w){
				w.notify();
			}
		} else if(command.equals("VOTE")){
			Float rank = new Float(0);
			String channelId = "";
			try{
				channelId = s.next();
				rank = new Float(s.next());
			}catch(NoSuchElementException e){}
			Float f = 1/rank;
			System.out.println("vote val: "+f);
			
			connection.vote(channelId, rank);

			synchronized(global.v){
				global.v.notify();
			}
		}else if(command.equals("PASSWORD")){
			String passTry ="";
			try{
				passTry = s.next();
			}catch(NoSuchElementException e){}
			String attempt="failed";
			if(passTry.equals(global.password)){
				this.isAdmin = true; 
				attempt = "passed";
				sendMessage("ADMINACCESS TRUE");
			}else{
				sendMessage("ADMINACCESS FALSE");
			}
			System.out.println("login attempt "+attempt);
		}else if(command.equals("POWER")){
			if(isAdmin){
				String mode = "";
				try{
					mode = s.next();
					int i = -1;
					if(mode.equals("ON")){
						i = 0;
					}else if(mode.equals("OFF")){
						i = 1;
					}
					if(i != -1)
						global.n.command(i);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(command.equals("TRIGGER")){
			String value="";
			try{
				value = s.next();
			}catch(Exception e){}
			System.out.println("sending trigger value");
			global.broadcast.sendAll(value);
		} else if(command.equals("TESTING")){
				adminCommands(s);
		}
		
	}
	
	private void adminCommands(Scanner s) {
		String command = s.next();
		if(command.equals("threadcount")){
			System.out.println("number of active threads: "+activeCount());;
		}
		
	}

	void sendChannel(int i){
		//CHANNEL <channel-id> <rank> <channel-name>
		String output[];
		output = global.list.getChannelCommand(i);
		
		String toSend = new String();
		for(int j = 0; j < output.length; j++){
			toSend += output[j];
			if(j < output.length -1)
				toSend += " ";
		}
		
		buf.add(toSend);
	}
	
	void sendMessage(String s){
		buf.add(s);
		synchronized(w){
			w.notify();
		}
	}
}
