import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class MonitorThread extends Thread{
	Socket requestSocket;
	PrintWriter out;
	BufferedReader in;
	InetAddress address;
	
	char powerStatusOut[];
	char powerOn[];
	char powerOff[];
	
	int operation;
	
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
	
	MonitorThread(String ip, int operation){
		try {
			address = InetAddress.getByName(ip);
			requestSocket = new Socket(address, 7142);
			out = new PrintWriter(requestSocket.getOutputStream(),true);
			out.flush();
			in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		char powerStatusOut[] = {SOH,'0','A','0','A','0','6',STX,'0','1','D','6',ETX,(char)116,'\r'};
		char powerOn[] = {SOH,'0','A','0','A','0','C',STX,'C','2','0','3','D','6','0','0','0','1',ETX,(char)115,'\r'};
		char powerOff[] = {SOH,'0','A','0','A','0','C',STX,'C','2','0','3','D','6','0','0','0','4',ETX,(char)118,'\r'};
		
		this.powerStatusOut = powerStatusOut;
		this.powerOn = powerOn;
		this.powerOff = powerOff;
		this.operation = operation;
	}
	
	public void run(){
		if(operation == TURN_ON)
			turnOn();
		if(operation == TURN_OFF)
			turnOff();
		if(operation == GET_STATUS);
			getStatus();
	}

	private int getStatus() {
		sendMessage(powerStatusOut);
		char c[] = getMessage();
		return decodePower(c);
		
	}

	private void turnOff() {
		int status = getStatus();
		if(status != POWER_OFF){
			sendMessage(powerOff);
			char c[] = getMessage();
		}
		
	}

	private void turnOn() {
		int status = getStatus();
		if(status != POWER_ON){
			sendMessage(powerOn);
			char c[] = getMessage();
		}
		
	}
	
	int decodePower(char c[]){
		char status=c[23];
		
		if(status == '1'){
			//System.out.println("power is on");
			return POWER_ON;
		}else if(status == '2'){
			//System.out.println("power is standby");
			return POWER_STANDBY;
		}else if(status == '3'){
			//System.out.println("power is suspend");
			return POWER_SUSPEND;
		}else{
			//System.out.println("power is off");
			return POWER_OFF;
		}
	}
	
	public void sendMessage(char c[]){		
		out.print(c);
		out.flush();
	}
	
	public char[] getMessage(){
		char c[] = new char[50];
		int numChars;
		try {
			numChars = in.read(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
