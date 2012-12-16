// John Wittrock | wittrock@gwu.edu
/*
 * This class is John's homework 3 part 3, with only slight modifications. 
 */

import java.io.*;
import java.util.*;

public class PasswordClassifier {

	static char getPrevChar(char c) {
		return ((char)(((int) (c)) - 1));
	}

	// returns true if password is strong, false otherwise
	public static boolean classify(String password) {
		try {	    
			File weakPasswords = new File("weakPasswords.txt");
			Scanner scanner = new Scanner(weakPasswords);
			HashSet<String> passwordSet = new HashSet<String>(30000);

			System.out.println("Reading in weak password list. This may take a little time.");
			while(scanner.hasNextLine()){
				passwordSet.add(scanner.nextLine().toLowerCase());
			}
			System.out.println("Finished reading in weak passwords. Size of set: " + passwordSet.size());


			if(passwordSet.contains(password.toLowerCase()) || password.length() < 6) {
				return false;
			}

			int length = password.length();
			int lowerCase = 0;
			int upperCase = 0;
			int numbers = 0;
			int symbols = 0;
			int letters = 0;
			int ascendingLetters = 0;
			int ascendingNumbers = 0;
			int repeatedNumbers = 0;
			int repeatedLetters = 0;
			int consecutiveLowerCase = 0;
			int consecutiveUpperCase = 0;
			int consecutiveNumbers = 0;
		
			for(int i = 0; i < password.length(); i++) {
				char c = password.charAt(i);
				if(Character.isLowerCase(c)) {
					lowerCase++;
					letters++;
				} else if (Character.isUpperCase(c)) {
					upperCase++;
					letters++;
				} else if (Character.isDigit(c)) {
					numbers++;
				} else {
					symbols++;
				}
		    
				if(i > 0) {
					char prev = password.charAt(i - 1);
					if(Character.isLetter(c)) {
						if(prev == getPrevChar(c)) 
							ascendingLetters++;
						if(prev == c)
							repeatedLetters++;
					} else if(Character.isDigit(c)) {
						if(prev == getPrevChar(c)) 
							ascendingNumbers++;
						if(prev == c)
							repeatedNumbers++;
					} 
			
					if(Character.isUpperCase(c) && Character.isUpperCase(prev)) {
						consecutiveUpperCase++;
					} else if(Character.isLowerCase(c) && Character.isLowerCase(prev)) {
						consecutiveLowerCase++;
					} else if(Character.isDigit(c) && Character.isDigit(prev)) {
						consecutiveNumbers++;
					}
				}
			}

			// Now we know something about the password.
		
			// If the password is all the same letter or number or 
			// is ascending letters or numbers, fail it instantly.
			if(ascendingLetters == length -1 || 
			   ascendingNumbers == length -1 || 
			   repeatedLetters == length -1 || 
			   repeatedNumbers == length -1) {
				//		    System.out.println(password + " 0");;
				return false;
			}

			// The scoring algorithm below was based on the NIST entropy tables
			// that we were given in class. Additional inspiration was provided 
			// by the assignment specification, which pointed me towards 
			// passwordmeter.com, which publishes a rough guide to their formula.
			// I've adapted some of their ideas to this algorithm.

			// Score will be a rough approximation of the entropy of the password.
			// The idea for calculating is as follows:
			// Minimum entropy (2 bits) for each character in the string plus:
			// a small amount of entropy (1 bit) for each lowercase
			// 2 bits for each uppercase
			// 3 bits for each number. All-numerals is a special (weak) case.
			// 3 bits for each symbol
		
			// Deductions are made for 
			// - repeated letters (half of the extra score)
			// - repeated numbers (half of the extra score)
			// - ascending numbers (all of the extra score)
			// - ascending letters (all of the extra score)
			// - consecutive numbers and letters 

			// The desired score is 26. (NIST + some modifications)

			int score = 0;

			score += length * 2;
			score += lowerCase;
			score += upperCase * 3;
			score += numbers * 4;
			score += symbols * 5;

			score -= repeatedLetters;
			score -= repeatedNumbers * 2;
			score -= ascendingNumbers * 3;
			score -= ascendingLetters;
			score -= consecutiveNumbers * 2;
			score -= consecutiveLowerCase;
			// score -= consecutiveUpperCase;

			if(numbers == length) {
				score = 2 * length;
			}

			//		System.out.println(password + " " + score);
			if(score >= 26){ 
				return true;
			} else {
				return false;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}