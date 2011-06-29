import java.util.ArrayList;


public class ConnectionManager {
	ArrayList<Connection> array;
	
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
	
	synchronized void updateAll(){
		
	}
}
