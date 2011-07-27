import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class WriteThread extends Thread{
	Socket socket;
	ServerSocket ss;
	PrintWriter out;
	ListManager list;
	SendBuffer buf;
	Boolean isConnected;
	
	WriteThread(Socket socket, ServerSocket ss, ListManager list, SendBuffer buf, Boolean isConnected){
		this.socket = socket;
		this.ss = ss;
		this.list = list;
		this.buf = buf;
		this.isConnected = isConnected;
		
		try{
			out = new PrintWriter(socket.getOutputStream(), true);
			out.flush();
		}catch(Exception e){}
	}
	synchronized public void run(){
		int size;
		String sendStr;
		sendMessage("connection successful");
		while(isConnected){
			while(buf.isEmpty && isConnected){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			synchronized(buf){
				if(isConnected){
					size = buf.numberOfItems();
					for(int i = 0; i < size; i ++){
						try {
							sendStr = buf.pop();
							System.out.println(sendStr);
							sendMessage(sendStr);
						} catch (EmptyStackException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		}
	}
	
	void sendMessage(String s){
		out.println(s);
		out.flush();
	}
}
