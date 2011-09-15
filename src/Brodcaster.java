import java.io.FileReader;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public class Brodcaster {
	
	URL urls[];
	ScriptEngineManager manager;
	ScriptEngine engine;
	FileReader reader;
	Invocable invokeEngine;
	
	Brodcaster(){
		urls = new URL[17];
		try{
			urls[0] = new URL("http://vw-player-r1c1:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[1] = new URL("http://vw-player-r1c2:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[2] = new URL("http://vw-player-r1c3:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[3] = new URL("http://vw-player-r1c4:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[4] = new URL("http://vw-player-r2c1:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[5] = new URL("http://vw-player-r2c2:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[6] = new URL("http://vw-player-r2c3:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[7] = new URL("http://vw-player-r2c4:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[8] = new URL("http://vw-player-r3c1:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[9] = new URL("http://vw-player-r3c2:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[10] = new URL("http://vw-player-r3c3:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[11] = new URL("http://vw-player-r3c4:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[12] = new URL("http://vw-player-r4c1:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[13] = new URL("http://vw-player-r4c2:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[14] = new URL("http://vw-player-r4c3:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[15] = new URL("http://vw-player-r4c4:8009/maxidrivers/maxisoftgpi/fire?gpi=");
			urls[16] = new URL("http://videowall:8009/maxidrivers/maxisoftgpi/fire?gpi=");
		} catch(Exception e){}
		
		manager = new ScriptEngineManager();
	    engine = manager.getEngineByName("rhino");
	    try{
	    	reader = new FileReader("ajax.js");
	    	engine.eval(reader);
	    	reader = new FileReader("Trigger.js");
	    	engine.eval(reader);
	    	invokeEngine = (Invocable) engine;
	    }catch(Exception e){}
	}
	
	/*
	 * used to send the same trigger number to all players simultaneously
	 */
	public synchronized void sendAll(String triggerNumber)
	{
		for(int i = 0; i < urls.length; i++){
			BrodcastThread b = new BrodcastThread(urls[i], invokeEngine, triggerNumber);
			b.start();
		}
	}
	
	/*
	 * sends a trigger to the specified url
	 * NOTE: best used for testing on the creator and not for videoWall
	 */
	public synchronized void sendOne(URL url, String triggerNumber){
		BrodcastThread b = new BrodcastThread(url, invokeEngine, triggerNumber);
		b.start();
	}
	
}
