
public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	voteThread(ListManager list, Brodcaster brodcast) {
		this.list = list;
		this.brodcast = brodcast;
	}
	public void run(){
		boolean changed = false;
		while(true){
			if(changed){
			 // TODO: do action if order changes
				changed = false;
			}
				changed = list.sortVote();
			 // TODO: set-up decay algorithm
			
		}
	}
}
