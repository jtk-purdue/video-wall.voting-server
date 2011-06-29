import java.net.ServerSocket;
import java.net.Socket;


public class Connection {
	Socket socket;
	ServerSocket ss;
	ListManager list;
	voteThread v;
	Brodcaster broadcast;
	Boolean isConnected;
	
	SendBuffer buf;
	WriteThread write;
	ReadThread read;
	
	Connection(Socket socket, ServerSocket ss, ListManager list, voteThread v, Brodcaster broadcast){
		this.socket = socket;
		this.ss = ss;
		this.list = list;
		this.v = v;
		this.broadcast = broadcast;
		
		isConnected = true;
		
		buf = new SendBuffer();
		write = new WriteThread(socket, ss, list, buf, isConnected);
		read = new ReadThread(socket, ss, list, broadcast, v, buf, write, isConnected);
		
		write.start();
		read.start();
	}
	
	public void clear(){
		//TODO: remove current connection from array of connections
	}
}
