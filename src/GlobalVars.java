import java.net.ServerSocket;


public class GlobalVars {
	ServerSocket ss;
	ListManager list;
	Brodcaster broadcast;
	voteThread v;
	NecManager n;
	String password;
	ConnectionManager connections;
	int secondsToWait;
	boolean isActive;
	public GlobalVars(ServerSocket ss, ListManager list,
			Brodcaster broadcast, voteThread v, NecManager n, String password,
			ConnectionManager connections, int secondsToWait, boolean isActive) {
		super();
		this.ss = ss;
		this.list = list;
		this.broadcast = broadcast;
		this.v = v;
		this.n = n;
		this.password = password;
		this.connections = connections;
		this.secondsToWait = secondsToWait;
		this.isActive = isActive;
	}
	
}
