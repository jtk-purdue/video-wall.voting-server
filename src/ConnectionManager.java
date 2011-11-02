import java.util.ArrayList;


public class ConnectionManager {
	ArrayList<Connection> array;
	
	ConnectionManager(){
		array = new ArrayList<Connection>();
	}
	
	synchronized void add(Connection c){
		array.add(c);
	}
	
	synchronized void remove(Connection c){
		c.isConnected = false;
		try {
			this.wait(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(c.write.isAlive()){
			WriteThread w = c.write;
			synchronized(w){
				w.notify();
				try {
					w.join();
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	synchronized void updateAll(String s){
		WriteThread w;
		Connection c;
		SendBuffer b;
		for(int i=0; i< array.size(); i++){
			c = array.get(i);
			if(c.isConnected()){
				w = c.write;
				b = c.buf;
				b.add(s);
				synchronized(w){
					w.notify();
				}
			}else{
				remove(c);
			}
		}
		
	}
}
