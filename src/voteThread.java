


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
						changed = false;
						System.out.println("Sending trigger for item: "+ list.getVote(0).id);
						//brodcast.sendAll(list.getVote(0).trigger);
				}
				
				try {
					synchronized(this){
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 // TODO: set-up decay algorithm
			
		}
	}
}
