package driver;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
	private static Scanner stdin;
	
	private static Process		encryptor, logger;
	private static Scanner 		encryptIS, loggerIS;
	private static PrintWriter	encryptOS, loggerOS;
	
	private static LinkedList<String> history = new LinkedList<String>();
	private static boolean password_set = false;
	
	public static void setPassMenu() {
		System.out.println("Password Not Set!");
		System.out.print("Select One Please:\n\t0 - Create New Password\n\t1 - Set From History\nSelection: ");
		
		int selection = Integer.valueOf(stdin.nextLine());
		if(history.size() == 0) {
			System.out.println("History is emtpy! Please Create a Password.");
			selection = 0;
		}
		
		switch(selection) {
			case 0:
				System.out.print("Enter Password: ");
				pipe("PASSKEY", stdin.nextLine());
				break;
			case 1: 
				int start = (history.size() > 10) ? history.size() - 10 : 0;
                for(int i = 0; i < history.size() && i < 10; i++) {
                    System.out.printf("%02d - %s\n", i, history.get(start + i));
                }
				
				System.out.print("Enter Selection: ");
				pipe("PASSKEY", history.get(history.size() - 1 - Integer.valueOf(stdin.nextLine())));
				break;
		}
		
		password_set = true;
	}
 	
	private static void pipe(String command, String argument) {
		// check if: user is encrypting or decrypting and if the password is set
		if(!command.equals("PASSKEY") && !password_set)
			setPassMenu();
		
		// output and log
		String out = String.format("%s %s", encryptIS.next(), argument);
		String log;
		
		System.out.println(out);
		
		// send to encryptor 
		encryptOS.printf("%s %s\n", command, argument);
		encryptOS.flush();
		
		// extract result and print it to both console and log file 
		out = encryptIS.nextLine().substring(1);
		log = out.substring(out.indexOf(' ') + 1);
		
		System.out.println(out);
		loggerOS.println(log);
		
		// history code
		if(!command.equals("PASSKEY")) {
			history.add(argument);
			history.add(log.substring(log.indexOf(' ') + 1));
		}
	}
	
	public static void main(String[] args) throws Exception {
		// check if correct number of arguments passed in
		
		if(args.length != 1)
			System.out.println("Invalid Number of Arguments");
		
		// fork and pipe encryptor and logger processes
		
		Runtime runtime = Runtime.getRuntime();
		
		encryptor = runtime.exec("java -jar res/encryptor.jar");
		encryptIS = new Scanner(encryptor.getInputStream());
		encryptOS = new PrintWriter(encryptor.getOutputStream());
		
		logger = runtime.exec("java -jar res/logger.jar " + args[0]);
		loggerIS = new Scanner(logger.getInputStream());
		loggerOS = new PrintWriter(logger.getOutputStream());
		
		// Logging
		
		loggerOS.println("START driver started");
		loggerOS.flush();
		
		// command loop
		
		stdin = new Scanner(System.in);
		String input, command, argument;
		int firstSpace;
		while(true) {
			// get command and argument from input
			System.out.print("Command: ");
			input = stdin.nextLine();
			firstSpace = input.indexOf(" ");

			if(firstSpace == -1) {
				command = input;
				argument = "";
				
			}
			else {
				command = input.substring(0, firstSpace);	
				argument = input.substring(firstSpace + 1);
			}
			
			// determine command
			if(command.equals("quit")) {
				break;
			}
			if(command.equals ("history")) {
				int start = (history.size() > 10) ? history.size() - 10 : 0;
                for(int i = 0; !history.isEmpty() && i < history.size() && i < 10; i++) {
                    System.out.printf("%02d - %s\n", i, history.get(start + i));
                }
				
				loggerOS.println("RESULT Printed History");
				continue;
			}
			// check if user inputed correct number of inputs
			if(argument.isBlank()) {
				history.add(command);
				loggerOS.println("RESULT Saved to History");
				continue;
			}
			
			if(command.equals("password")) {
				pipe("PASSKEY", argument);
				password_set = true;
				continue;
			}
			if(command.equals("encrypt")) {
				pipe("ENCRYPT", argument);
				continue;
			}
			if(command.equals("decrypt")) {
				pipe("DECRYPT", argument);
				continue;
			}
		}
		stdin.close();
		
		// logging and close programs 
		
		encryptOS.println("QUIT");
		encryptOS.flush();
		
		loggerOS.println("STOP Stopped Driver");
		loggerOS.println("QUIT");
		loggerOS.flush();
		
		// close the child processes
		
		encryptIS.close();
		encryptOS.close();
		
		loggerIS.close();
		loggerOS.close();
		
		System.out.println("Waiting for Children to Exit");
		while(logger.isAlive() || encryptor.isAlive());
		System.out.println("Children exited");
	}
}