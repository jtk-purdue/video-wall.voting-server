


public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	long startTime = System.currentTimeMillis();
	ConnectionManager connections;
	voteThread(ListManager list, Brodcaster brodcast, ConnectionManager connections) {
		this.list = list;
		this.brodcast = brodcast;
		this.connections = connections;
	}
	public void run(){
		boolean changed = false;
		String first[];
		String second[];
		first = list.getStringList();
		while(true){
				
				changed = list.sortVote();
				if(changed){
					second = list.getStringList();
					for(int i=0; i < second.length; i++){
						if(!second[i].equals(first[i])){
							sendRank(second[i], i);
						}
					}
					first = second;
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
	
	void sendRank(String channelID, int rank){
		String message = "RANK "+channelID+" "+(rank+1);
		connections.updateAll(message);
	}
}
