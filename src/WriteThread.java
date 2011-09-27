import java.io.PrintWriter;
import java.net.Socket;


public class WriteThread extends Thread{
	Socket socket;
	PrintWriter out;
	SendBuffer buf;
	Boolean isConnected;
	GlobalVars global;
	
	WriteThread(Socket socket, GlobalVars global, SendBuffer buf, Boolean isConnected){
		this.socket = socket;
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
					e.printStackTrace();
				} 
			}
			synchronized(buf){
				if(isConnected){
					size = buf.numberOfItems();
					for(int i = 0; i < size; i ++){
						try {
							sendStr = buf.pop();
							if(sendStr.indexOf("RANK") != 0)
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
