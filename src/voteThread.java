import java.net.MalformedURLException;
import java.net.URL;




public class voteThread extends Thread {
	GlobalVars global;
	long lastChange;
	long waitTimeMillis;
	URL myUrl;
	
	voteThread() {
		lastChange = System.currentTimeMillis()- waitTimeMillis;
		try {
			myUrl = new URL("http://videowall:8009/maxidrivers/maxisoftgpi/fire?gpi=");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setGlobal(GlobalVars global){
		this.global = global;
		this.waitTimeMillis = this.global.secondsToWait * 1000;
	}
	
	public void run(){
		boolean changed = false;
		boolean needToChange = false;
		String first[];
		String second[];
		first = global.list.getStringList();
		while(true){
				
			changed = global.list.sortVote();
			if(changed){
				needToChange = true;
				second = global.list.getStringList();
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
					
					if(global.isActive){
						System.out.println("Active: Sending trigger for: "+global.list.getVote(0).name);
						global.broadcast.sendOne(myUrl, global.list.getVote(0).trigger);
					}else{
						System.out.println("Sending trigger for item: "+ global.list.getVote(0).id);
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
		System.out.println(message);
		global.connections.updateAll(message);
	}
}
