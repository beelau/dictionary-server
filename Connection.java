import java.io.*;
import java.net.Socket;

public class Connection {

	private boolean connected;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
		
	public Connection(Socket socket, PrintWriter out, BufferedReader in) {
		this.socket = socket;
		this.in = in;
		this.out = out;	
		this.connected = false;
	}

	public static Connection connect(String host, int port, boolean debugOn) throws IOException {

    	try {  		
	    		Socket socket = new Socket(host, port);
	    		socket.setSoTimeout(30 * 1000);
	    		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	
    			Connection connection = new Connection(socket, out, in);
	    		String fromServer = in.readLine();
	    		if (fromServer.startsWith("220")) {
	    			if (debugOn)
	    				System.out.println(fromServer);
	    			connection.connected = true;
		    		return connection;
	    		} else {
	    			System.err.println("999 Processing error. Host is not running a DICT server");
	    			socket.close();
	    		}	    			
	    	} catch (IOException exception) {
	    		System.err.println("920 Control connection to "+ host +  " on port " + port + " failed to open.");
	    	}

    	return null;		
	}
	
	public static Connection close(Connection connection, boolean debugOn) throws IOException {
		
		Socket socket = connection.socket;
		PrintWriter out = connection.out;
		BufferedReader in = connection.in;
			
		String fromServer;
		try {
		    if (debugOn)
                System.out.println("--> QUIT");
			out.println("QUIT");
			fromServer = in.readLine();
			if (fromServer.startsWith("221")){
				socket.close();
				out.close();
				in.close();
				connection.connected = false;
				if (debugOn)
					System.out.println("<-- " + fromServer);
			}
			
		} catch (IOException exception) {
			
		}
		return connection;
	}

	public boolean isConnected(){
		return this.connected;
	}
		
	public Socket getSocket(){
		return this.socket;
	}
	
	public BufferedReader getIn(){
		return this.in;
	}
	
	public PrintWriter getOut() {
		return this.out;
	}
}
