import java.io.*;
import java.net.Socket;

public class DictProtocol {
	
	
	public static void printDictionaryList(Connection connection, boolean debugOn) throws IOException {
		
		BufferedReader in = connection.getIn();
		PrintWriter out = connection.getOut();
		String fromServer;
		try {
		    if (debugOn)
                System.out.println("--> SHOW DB");
			out.println("SHOW DB");
			fromServer = in.readLine();
			if (fromServer.startsWith("110")){
				if (debugOn){
					System.out.println(fromServer);
				}
				while ((fromServer = in.readLine()) != null){
					if (fromServer.startsWith("250")){
						if (debugOn){
							System.out.println(fromServer);
						}
						break;
					} else {
						System.out.println(fromServer);
					}
				}
			}
		} catch (IOException exception) {
			System.err.println("999 Processing error. Couldn't read dictionary");
		}
	}	
	
	public static void define(Connection connection, String dictDB, String word, boolean debugOn){
	    BufferedReader in = connection.getIn();
        PrintWriter out = connection.getOut();
        String fromServer;
        String cmd = "DEFINE " + dictDB + " " + word;
        try {
            if (debugOn)
                System.out.println("--> " + cmd);
            out.println(cmd);
            fromServer = in.readLine();
            if (fromServer.startsWith("150")) {
                if (debugOn)
                    System.out.println("<-- " + fromServer);
                while ((fromServer = in.readLine()) != null){
                    if (debugOn)
                        System.out.print("<-- ");
                    if (fromServer.startsWith("250")){
                        if (debugOn){
                            System.out.println(fromServer);
                        } break;
                    } else if (fromServer.startsWith("151")){
                        if (debugOn)
                            printDefnWithStatus(fromServer, word.length());
                    } else
                        System.out.println(fromServer);
                }
            }
            else if (fromServer.startsWith("552")) {
                if (debugOn)
                    System.out.println("<-- " + fromServer);
                System.out.println("***No definition found***");
                match(connection, dictDB, ".", word, debugOn, true);
            }
            else if (fromServer.startsWith("550"))
                System.out.println("902 Invalid argument.");
        } catch (IOException exception) {
            System.err.println("999 Processing error. Couldn't read from connection.");
        }
	}
	
	public static void match(Connection connection, String dictDB, String strat, String word, boolean debugOn, boolean postDefine) {
	    BufferedReader in = connection.getIn();
        PrintWriter out = connection.getOut();
        String fromServer;
        String cmd = "MATCH " + dictDB + " " + strat + " " + word;
        try {
            if (debugOn)
                System.out.println("--> " + cmd);
            out.println(cmd);
            fromServer = in.readLine();
            if (fromServer.startsWith("152")) {
                if (debugOn)
                    System.out.println("<-- " + fromServer);
                while ((fromServer = in.readLine()) != null){
                    if (debugOn)
                        System.out.print("<-- ");
                    if (fromServer.startsWith("250")){
                        if (debugOn){
                            System.out.println(fromServer);
                        } break;
                    } else
                        System.out.println(fromServer);
                }        
            } 
            else if (fromServer.startsWith("552")) {
                if (debugOn)
                    System.out.println("<-- " + fromServer);
                if (postDefine)
                    System.out.println("****No matches found****");
                else
                    System.out.println("*****No matching word(s) found*****");
            }
            else if (fromServer.startsWith("550"))
                System.out.println("902 Invalid argument.");
        } catch (IOException exception) {
            System.err.println("999 Processing error. Couldn't read from connection.");
        }
	}
	
	public static void printWithoutStatus(String fromServer) {
        System.out.println(fromServer.substring(4));
    }
	
	public static void printDefnWithStatus(String fromServer, int wordLength) {
	    System.out.println("@ " + fromServer.substring(0, 4) + fromServer.substring(wordLength+7));
	}
}
	

