
public class NecManager {

	public final char SOH = (char)01;
	public final char STX = (char)02;
	public final char ETX = (char)03;
	
	public final int POWER_OFF = 0;
	public final int POWER_ON = 1;
	public final int POWER_STANDBY = 2;
	public final int POWER_SUSPEND = 3;

	public final int TURN_ON = 0;
	public final int TURN_OFF = 1;
	public final int GET_STATUS = 2;
	boolean run;
	
	NecManager(boolean run){
		this.run = run;
	}
	
	void command(int operation){
		//0 on
		//1 off
		if(run){
			MonitorThread t11 = new MonitorThread("128.10.131.24" ,operation);
			MonitorThread t12 = new MonitorThread("128.10.131.25" ,operation);
			MonitorThread t13 = new MonitorThread("128.10.131.26" ,operation);
			MonitorThread t14 = new MonitorThread("128.10.131.27" ,operation);
		
			MonitorThread t21 = new MonitorThread("128.10.131.28" ,operation);
			MonitorThread t22 = new MonitorThread("128.10.131.29" ,operation);
			MonitorThread t23 = new MonitorThread("128.10.131.30" ,operation);
			MonitorThread t24 = new MonitorThread("128.10.131.31" ,operation);
			
			MonitorThread t31 = new MonitorThread("128.10.131.32" ,operation);
			MonitorThread t32 = new MonitorThread("128.10.131.33" ,operation);
			MonitorThread t33 = new MonitorThread("128.10.131.34" ,operation);
			MonitorThread t34 = new MonitorThread("128.10.131.35" ,operation);
			
			MonitorThread t41 = new MonitorThread("128.10.131.36" ,operation);
			MonitorThread t42 = new MonitorThread("128.10.131.37" ,operation);
			MonitorThread t43 = new MonitorThread("128.10.131.38" ,operation);
			MonitorThread t44 = new MonitorThread("128.10.131.39" ,operation);
			t11.run();
			t12.run();
			t13.run();
			t14.run();
			
			t21.run();
			t22.run();
			t23.run();
			t24.run();
			
			t31.run();
			t32.run();
			t33.run();
			t34.run();
			
			t41.run();
			t42.run();
			t43.run();
			t44.run();
		}else{
			String s;
			if(operation == 0)
				s = "Monitors turned on";
			else if(operation == 1)
				s = "Monitors turned off";
			else
				s = "Bad input to monitor controller";
			System.out.println("Test Response: "+s);
		}
		
	}
}
