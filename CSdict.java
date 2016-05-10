
import java.lang.System;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CSdict
{
    static final int MAX_LEN = 255;
    static final int PERMITTED_ARGUMENT_COUNT = 1;
    static boolean debugOn = false;
    static Socket socket;
    static Connection connection;
    static String dictDB;
    
    public static void main(String [] args)
    {
	byte cmdString[] = new byte[MAX_LEN];
	
	if (args.length == PERMITTED_ARGUMENT_COUNT) {
	    debugOn = args[0].equals("-d");
	    if (debugOn) {
	    	debugOn = true;
	    	System.out.println("Debugging output enabled");
	    } else {
	    	System.out.println("997 Invalid command line option - Only -d is allowed");
		return;
            } 
	} else if (args.length > PERMITTED_ARGUMENT_COUNT) {
	    System.out.println("996 Too many command line options - Only -d is allowed");
	    return;
	}
		
	try {
	    for (int len = 1; len > 0;) {
			System.out.print("csdict> ");
			len = System.in.read(cmdString);
			if (len <= 0)
			    break;
			
			// Start processing the command here.
			InputStream inputStream = new ByteArrayInputStream(cmdString); 
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(inputStream));
		    String str = bufferRead.readLine();	    
		    String[] strSplit = str.split("\\s+");
		    int strLength = strSplit.length;
		    String cmnd = "";
		    if (strLength > 0){
		    	cmnd = strSplit[0];
		    } 
		    
		    if (cmnd.equals("quit")){
		    	if (connection != null)
		    		Connection.close(connection, debugOn);
		    	break;
		    } 
		    
		    else if (cmnd.equals("close")){
		    	if (connection != null && connection.isConnected() == true) {
		    		connection = Connection.close(connection, debugOn);
		    	} else {
		    		System.err.println("903 Supplied command not expected at this time.");
		    	}
		    } 
		    
		    else if (cmnd.equals("open")) {
		        if (strLength < 2 || strLength > 3) {
                        System.out.println("901 Incorrect number of arguments.");
		        }
		        else if (connection == null || connection.isConnected() == false) {
		        	String host = strSplit[1];
		        	int port = 0;		        	        				        	  	        	
		        	if (strLength == 3){
			    		if (isNumeric(strSplit[2])){
			    			port = Integer.parseInt(strSplit[2]);
			    		} else {
			    			System.err.println("999 Processing error. Port is not numeric.");
			    		}	    
		        	} else {
		        		port = 2628;
		        	}	
	       
		        	if (port > 0){
		        		connection = Connection.connect(host, port, debugOn);
			    		dictDB = "*";
		        	}
			        
		    	} else 
		    		System.out.println("903 Supplied command not expected at this time.");
		    } 	        
		    else {
		    	if (connection == null || connection.isConnected() == false) {
		    		System.out.println("903 Supplied command not expected at this time.");
		    	}
		    	else {
			    	if (cmnd.equals("dict")){
			    	    if (strLength != 1)
	                        System.out.println("901 Incorrect number of arguments.");
	                    else 
	                        DictProtocol.printDictionaryList(connection, debugOn);
			    	}
			    	else if (cmnd.equals("set")) {
			    	    if (strLength != 2)
			    	        System.out.println("901 Incorrect number of arguments.");
			    	    else 
			    	        dictDB = strSplit[1];
			    	}
			    	else if (cmnd.equals("define")){
			    	    if (strLength != 2)
			    	        System.out.println("901 Incorrect number of arguments.");
			    	    else
			    	        DictProtocol.define(connection, dictDB, strSplit[1], debugOn);
			    	}
			    	else if (cmnd.equals("match")){
			    	    if (strLength != 2)
	                        System.out.println("901 Incorrect number of arguments.");
	                    else
	                        DictProtocol.match(connection, dictDB, "exact", strSplit[1], debugOn, false);
			    	}
			    	else if (cmnd.equals("prefixmatch")) {
			    		if (strLength != 2)
	                        System.out.println("901 Incorrect number of arguments.");
	                    else
	                        DictProtocol.match(connection, dictDB, "prefix", strSplit[1], debugOn, false);
			    	}
			    	else if (cmnd.isEmpty() || cmnd.startsWith("#"))
	                    continue;
			    	else 
			    	    System.out.println("900 Invalid command.");
		    	}
		    }

	    } // end of for loop
	    
	} catch (IOException exception) {
	    System.err.println("998 Input error while reading commands, terminating.");
	}
    }

	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	

}
