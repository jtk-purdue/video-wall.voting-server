
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
				changed = sortList();
			 // TODO: set-up decay algorithm
			
		}
	}
	
	boolean sortList(){
		boolean changed = false;
		boolean unsorted = false;
		String tmpName;
		float tmpVote;
		int size = list.getListSize();
		int end = size-1;
		for(int i=0; i < size - 1; i++)
		{
			if(list.get(i).vote < list.get(i+1).vote)
				unsorted = true;
		}
		if(unsorted){
			changed = true;
			for(int i = 0; i < size; i++)
			{
				for(int j = 0; j < end; j++){
					if(list.get(j).vote < list.get(j+1).vote){
						tmpName = list.get(j).name;
						tmpVote = list.get(j).vote;
						list.get(j).name = list.get(j+1).name;
						list.get(j).vote = list.get(j+1).vote;
						list.get(j+1).name = tmpName;
						list.get(j+1).vote = tmpVote;
					}
				}
				end--;
			}
		}
		return changed;
	}
}
