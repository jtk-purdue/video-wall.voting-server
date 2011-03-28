import java.text.SimpleDateFormat;
import java.util.Date;


public class UpdateTracker {
	Date lastUpdate;
	
	UpdateTracker(){
		lastUpdate = new Date();
	}
	
	boolean needUpdate(String s){
		//returns true if the phone needs to update their list;
		SimpleDateFormat simp = new SimpleDateFormat();
		Date last;
		try{
			last = simp.parse(s);
			return lastUpdate.after(last);
		}catch(Exception e){}
		
		return true;
	}
	
	void update(){
		lastUpdate = new Date();
	}
}
