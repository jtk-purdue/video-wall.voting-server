
public class UpdateTracker {
	Long lastUpdate;
	
	UpdateTracker(){
		lastUpdate = Long.valueOf(System.currentTimeMillis());
	}
	
	boolean needUpdate(String s){
		//returns true if the phone needs to update their list;
		Long last;
		try{
			last = Long.valueOf(s);
			System.out.println("System: "+lastUpdate+" Phone: "+last);
			return ((lastUpdate - last) <= 1000);
		}catch(Exception e){}
		
		return false;
	}
	
	void update(){
		lastUpdate = Long.valueOf(System.currentTimeMillis());
	}
}
