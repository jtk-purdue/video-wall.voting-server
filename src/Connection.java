import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Connection {
	Socket socket;
	ServerSocket ss;
	ListManager list;
	voteThread v;
	Brodcaster broadcast;
	Boolean isConnected;
	
	String password;
	
	SendBuffer buf;
	WriteThread write;
	ReadThread read;
	
	NecManager n;
	ConnectionManager connections;
	
	ArrayList<String> votes;
	
	Connection(Socket socket, ServerSocket ss, ListManager list, voteThread v, Brodcaster broadcast, NecManager n, String password, ConnectionManager connections){
		this.socket = socket;
		this.ss = ss;
		this.list = list;
		this.v = v;
		this.broadcast = broadcast;
		this.n = n;
		this.connections = connections;
		isConnected = true;
		
		votes = new ArrayList<String>();
		
		buf = new SendBuffer();
		write = new WriteThread(socket, ss, list, buf, isConnected);
		read = new ReadThread(socket, ss, list, broadcast, v, buf, write, isConnected, this, n, password);
		
		write.start();
		read.start();
		
	}
	
	public void vote(String id, Float rank){
		int index;
		Float f;
		String s;
		if(list.find(id) != -1){
			for(int i=0; i < votes.size(); i++){
				s = votes.get(i);
				if(!s.equals("null")){
					f = new Float(1/(float)(i+1));
					list.unVote(s, f);
				}
			}
			if(rank >= votes.size()){
				while(rank >= votes.size()){
					votes.add("null");
				}
			}
			if(votes.contains(id)){
				index = votes.indexOf(id);
				votes.remove(index);
			}else if(votes.get(rank.intValue()).equals("null")){
				votes.remove(rank.intValue());
			}else{
				int j = rank.intValue()-1;
				int size = votes.size();
				
				for(int i=j; i < size ;i++){
					if(votes.get(i).equals("null")){
						votes.remove(i);
						break;
					}
				}
			}
			if(rank != -1)
				votes.add((int)(rank-1), id);
			for(int i=0; i < votes.size(); i++){
				s = votes.get(i);
				if(!s.equals("null")){
					f = 1/((float)i+1);
					list.vote(s, f);
				}
				
			}
		}
	}
	
	public void clear(){
		String s;
		Float f;
		for(int i=0; i < votes.size(); i++){
			s = votes.get(i);
			if(!s.equals("null")){
				f = new Float(1/(float)(i+1));
				list.unVote(s, f);
			}
		}
		synchronized(v){
			v.notify();
		}
		
		connections.remove(this);
	}
	
	public void test(){
		System.out.println("connection test successful");
	}
}
