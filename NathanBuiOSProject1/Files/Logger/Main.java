package logger;

import java.sql.Timestamp;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		if(args.length == 0)
			return;
		
		File logfile = new File(args[0]);
		FileWriter logwriter = new FileWriter(logfile, true);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String action = "START";
		String message = "Logging Started";
		
		String line = String.format("%s [%s] %s\n", timestamp.toString(), action, message);
		logwriter.write(line, 0, line.length());
		
		Scanner stdin = new Scanner(System.in);
		String input;
		int firstSpace;
		while(!action.equals("QUIT")) {
			// get action and message from the input
			System.out.print("Log Message (Enter 'QUIT' to quit): ");
			input = stdin.nextLine();
			firstSpace = input.indexOf(" ");
			
			if(firstSpace == -1) {
				action = input;
				message = "";
			}
			else {
				action = input.substring(0, firstSpace);
				message = input.substring(firstSpace + 1);
			}
			
			// determine command
			if(action.equalsIgnoreCase("QUIT"))
				break;
			
			timestamp.setTime(System.currentTimeMillis());
			
			line = String.format("%s [%s] %s\n", timestamp.toString(), action, message);
			logwriter.write(line, 0, line.length());
		}
		stdin.close();
		
		timestamp.setTime(System.currentTimeMillis());
		action = "STOP";
		message = "Logging Stopped";
		
		line = String.format("%s [%s] %s\n", timestamp.toString(), action, message);
		logwriter.write(line, 0, line.length());
		
		logwriter.flush();
		logwriter.close();
	}
}
