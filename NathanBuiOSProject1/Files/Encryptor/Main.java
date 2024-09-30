package encryptor;

import java.util.Scanner;

public class Main {
	public static char roll(char character, int amount) {
		boolean isUpperCase = Character.isUpperCase(character);
		
		character = Character.toUpperCase(character);
		character += amount;
		
		if(character < 'A') 
			character += 26;
		if(character > 'Z')
			character -= 26;
		
		return (isUpperCase) ? character : Character.toLowerCase(character);
	}
	
	public static String encrypt(String plaintext, String passkey) {
		String cyphertext = "";
		int amount;
		
		for(int i = 0; i < plaintext.length(); i++) {
			amount = passkey.charAt(i % passkey.length()) - 'A'; 
			cyphertext += roll(plaintext.charAt(i), amount);
		}
		
		return cyphertext;
	}
	
	public static String decrypt(String cyphertext, String passkey) {
		String plaintext = "";
		int amount;
		
		for(int i = 0; i < cyphertext.length(); i++) {
			amount = 'A' - passkey.charAt(i % passkey.length());
			plaintext += roll(cyphertext.charAt(i), amount);
		}
		
		return plaintext;
	}
	
	public static void output(String message) {
		System.out.printf("Output: %s\n", message);
	}
	
	public static void main(String[] args) {
		String passkey = null;
		
		Scanner stdin = new Scanner(System.in);
		while(true) {
			System.out.print("Input: ");
			String[] inputs = stdin.nextLine().split(" ");
			
			// make sure user actually entered something
			
			if(inputs[0].equals("QUIT"))
				break;
			if(inputs[0].equals("PASSKEY"))
				passkey = inputs[1].toUpperCase();
				output("RESULT");

			// make sure user entered sufficent number of arguments
			if(inputs.length < 2)
				output("ERROR Not enough arguments inputed");
			
			if(inputs[0].equals("ENCRYPT"))
				if(passkey == null)
					output("ERROR Password not set");
				else
					output("RESULT " + encrypt(inputs[1], passkey));
			if(inputs[0].equals("DECRYPT"))
				if(passkey == null)
					output("ERROR Password not set");
				else
					output("RESULT " + decrypt(inputs[1], passkey));
		}
		stdin.close();
	}
}
