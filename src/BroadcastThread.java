import java.net.URL;

import javax.script.Invocable;


public class BroadcastThread extends Thread{
	URL url;
	Invocable invokeEngine;
	String triggerNumber;
	BroadcastThread(URL url, Invocable invokeEngine, String triggerNumber ){
		this.url = url;
		this.invokeEngine = invokeEngine;
		this.triggerNumber = triggerNumber;
	}
	public void run(){
		try{
			Object o = invokeEngine.invokeFunction("fire",url, triggerNumber);
			System.out.println(o);
		}catch(Exception e){e.printStackTrace();}
	}
}
