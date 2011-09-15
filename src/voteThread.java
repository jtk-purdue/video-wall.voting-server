


public class voteThread extends Thread {
	ListManager list;
	Brodcaster brodcast;
	long lastChange;
	ConnectionManager connections;
	boolean isActive;
	
	voteThread(ListManager list, Brodcaster brodcast, ConnectionManager connections, boolean isActive) {
		this.list = list;
		this.brodcast = brodcast;
		this.connections = connections;
		this.isActive = isActive;
		lastChange = System.currentTimeMillis()- 30000;
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
			
			if((System.currentTimeMillis() - lastChange) >= 30000){
				if(needToChange){
					
					if(isActive){
						brodcast.sendAll(list.getVote(0).trigger);
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
						wait(30000 - (System.currentTimeMillis() - lastChange));
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
