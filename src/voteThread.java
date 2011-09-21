import java.net.MalformedURLException;
import java.net.URL;




public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	long lastChange;
	ConnectionManager connections;
	boolean isActive;
	long waitTimeMillis;
	URL myUrl;
	
	voteThread(ListManager list, Brodcaster brodcast, ConnectionManager connections, boolean isActive, int secondsToWait) {
		this.list = list;
		this.brodcast = brodcast;
		this.connections = connections;
		this.isActive = isActive;
		this.waitTimeMillis = secondsToWait * 1000;
		lastChange = System.currentTimeMillis()- waitTimeMillis;
		try {
			myUrl = new URL("http://videowall:8009/maxidrivers/maxisoftgpi/fire?gpi=");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		boolean changed = false;
		boolean needToChange = false;
		String first[];
		String second[];
		first = list.getStringList();
		while(true){
				
			changed = list.sortVote();
			if(changed){
				needToChange = true;
				second = list.getStringList();
				for(int i=0; i < second.length; i++){
					if(!second[i].equals(first[i])){
						sendRank(second[i], i);
					}
				}
				first = second;
				changed = false;
			}
			
			if((System.currentTimeMillis() - lastChange) >= waitTimeMillis){
				if(needToChange){
					
					if(isActive){
						System.out.println("Active: Sending trigger for: "+list.getVote(0).name);
						brodcast.sendOne(myUrl, list.getVote(0).trigger);
					}else{
						System.out.println("Sending trigger for item: "+ list.getVote(0).id);
					}
					needToChange = false;
					lastChange = System.currentTimeMillis();
				} else {
					try{
						synchronized(this){
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				synchronized(this){
					try {
						wait(waitTimeMillis - (System.currentTimeMillis() - lastChange));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	void sendRank(String channelID, int rank){
		String message = "RANK "+channelID+" "+(rank+1);
		connections.updateAll(message);
	}
}
