import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;


public class ReadThread extends Thread {
	Socket socket;
	ServerSocket ss;
	BufferedReader in;
	ListManager list;
	Brodcaster broadcast;
	voteThread v;
	SendBuffer buf;
	WriteThread w;
	Boolean isConnected;

	ReadThread(Socket socket, ServerSocket ss, ListManager list, Brodcaster broadcast, voteThread v, SendBuffer buf, WriteThread w, Boolean isConnected){
		this.socket = socket;
		this.ss = ss;
		this.list = list;
		this.broadcast = broadcast;
		this.v = v;
		this.buf = buf;
		this.w = w;
		this.isConnected = isConnected;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: remove from list of connections
			//TODO: set 'isConnected' to false or do in Connection
		}
	}
	
	public void run(){
		String message = new String();
		
		while(isConnected){
			try {
				message = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try{
				if(!message.equals(""))
					process(message);
			}catch(NullPointerException e){
				isConnected = false;
				synchronized(w){
					w.isConnected = false;
					w.notify();
				}
			}
		}
	}
	
	void process(String message){
		System.out.println(Calendar.getInstance().getTime() +":"+socket.getInetAddress()+"--"+ message);
		Scanner s = new Scanner(message);
		String command = s.next();
		if(command.equals("GETLIST")){
			for(int i = 0; i < list.getListSize(); i++){
				sendChannel(list.get(i));
			}
			synchronized(w){
				w.notify();
			}
		} else if(command.equals("VOTE")){
			String channelId = s.next();
			Integer rank = new Integer(s.next());
			list.vote(channelId);
			//TODO: Create single transferable voting system
			synchronized(v){
				v.notify();
			}
		}
	}
	
	void sendChannel(VoteItem vi){
		//CHANNEL <channel-id> <rank> <channel-name>
		String output[];
		output = new String[4];
		output[0] = "CHANNEL";
		output[1] = vi.id;
		output[2] = vi.vote+"";
		output[3] = vi.name;
		
		String toSend = new String();
		for(int i = 0; i < output.length; i++){
			toSend += output[i];
			if(i < output.length -1)
				toSend += " ";
		}
		
		buf.add(toSend);
	}
}
