
public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	long startTime = System.currentTimeMillis();
	voteThread(ListManager list, Brodcaster brodcast) {
		this.list = list;
		this.brodcast = brodcast;
	}
	public void run(){
		boolean changed = false;
		while(true){
				
				changed = list.sortVote();
				if(changed){
					 // TODO: do action if order changes
						changed = false;
				}
				
				try {
					synchronized(this){
						wait(300000 - (System.currentTimeMillis() - startTime));
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
