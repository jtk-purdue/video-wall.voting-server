import java.util.ArrayList;


public class SendBuffer {
	ArrayList<String> list;
	int numInBuffer;
	Boolean isEmpty;
	
	SendBuffer(){
		list = new ArrayList<String>();
		isEmpty = true;
		numInBuffer = 0;
	}
	
	synchronized String pop() throws EmptyStackException{
		if(numInBuffer == 0){
			throw new EmptyStackException();
		}else{
			String out = list.get(0);
			list.remove(0);
			numInBuffer--;
			if(numInBuffer == 0){
				isEmpty = true;;
			}
			return out;
		}
	}
	
	synchronized int numberOfItems(){
		return numInBuffer;
	}
	
	synchronized void add(String s){
		numInBuffer++;
		list.add(s);
		isEmpty = false;
	}
	synchronized void add(String s[]){
		for(int i = 0; i < s.length; i++)
			list.add(s[i]);
		isEmpty = false;
	}
}

class EmptyStackException extends Exception{
	private static final long serialVersionUID = 1L;
}