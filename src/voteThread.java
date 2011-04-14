


public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	long startTime = System.currentTimeMillis();
	UpdateTracker lastUpdate;
	voteThread(ListManager list, Brodcaster brodcast, UpdateTracker lastUpdate) {
		this.list = list;
		this.brodcast = brodcast;
		this.lastUpdate = lastUpdate;
	}
	public void run(){
		boolean changed = false;
		while(true){
				
				changed = list.sortVote();
				if(changed){
						changed = false;
						brodcast.sendAll(list.getVote(0).trigger);
				}
				
				try {
					synchronized(this){
						wait(300000 - (System.currentTimeMillis() - startTime));
						lastUpdate.update();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if((System.currentTimeMillis() - startTime) >= 300000){
					decay();
				}
			 // TODO: set-up decay algorithm
			
		}
	}
	
	public void decay(){
		//votes*.9 every 5 minutes
		list.decayAll();
		startTime = System.currentTimeMillis();
	}
}
