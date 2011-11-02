import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class Connection {
	Socket socket;
	Boolean isConnected;
		
	SendBuffer buf;
	WriteThread write;
	ReadThread read;
	
	GlobalVars global;
		
	ArrayList<String> votes;
	
	public Connection(Socket socket, GlobalVars global) {
		isConnected = true;
		
		votes = new ArrayList<String>();
		this.global = global;
		
		buf = new SendBuffer();
		write = new WriteThread(socket, global, buf, isConnected);
		read = new ReadThread(socket, global, buf, write, isConnected, this);
		
		write.start();
		read.start();
	}

	public void vote(String id, Float rank){
		int index;
		Float f;
		String s;
		if(global.list.find(id) != -1){
			for(int i=0; i < votes.size(); i++){
				s = votes.get(i);
				if(!s.equals("null")){
					f = new Float(1/(float)(i+1));
					global.list.unVote(s, f);
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
					global.list.vote(s, f);
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
				global.list.unVote(s, f);
			}
		}
		synchronized(global.v){
			global.v.notify();
		}
		
		global.connections.remove(this);
	}
	
	public void test(){
		System.out.println("connection test successful");
	}
	
	public boolean isConnected(){
		if(write.socket.isConnected() && read.socket.isConnected())
			return true;
		return false;
	}
}
