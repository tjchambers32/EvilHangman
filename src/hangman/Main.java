package hangman;

import hangman.IEvilHangmanGame.GuessAlreadyMadeException;

import java.io.File;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		// Usage: java [your main class name] dictionary wordLength guesses
		if (args.length != 3) {
			System.out
					.println("Usage: java [your main class name] dictionary wordLength guesses");
			return;
		}

		EvilHangmanGame game = new EvilHangmanGame();
		int wordLength = Integer.parseInt(args[1]);
		int guesses = Integer.parseInt(args[2]);
		if (wordLength < 2 || guesses < 1) {
			System.out.println("Must have wordLength >= 2 and guesses >= 1");
			return;
		}
		game.startGame(new File(args[0]), wordLength);
		
		Set<String> guessedSoFar = new TreeSet<String>();
		Set<String> possibleWords = new HashSet<String>();
		@SuppressWarnings("resource")
		Scanner guess = new Scanner(System.in);
		StringBuilder word = new StringBuilder();
		
		for (int i = 0; i < wordLength; i++) {
			word.append("-");
		}

		boolean winner = false;
		for (int i = 0; i < guesses; i++) {
			winner = false;
			System.out.println("You have " + (guesses - i) + " guesses left");
			System.out.println("Used letters: " + guessedSoFar.toString());
			System.out.println("Word: " + word);

			String unrefinedGuess = "a";
			System.out.print("Enter guess: ");
			unrefinedGuess = guess.nextLine();
			if (!unrefinedGuess.matches("[a-zA-Z]{1}")) {
				System.out.println("Invalid Guess: " + "\""
						+ unrefinedGuess + "\"" + " try again.");
				i--;
				continue;
			}
			unrefinedGuess = unrefinedGuess.toLowerCase();
			char currentGuess = unrefinedGuess.charAt(0);
			
			try {
				possibleWords = game.makeGuess(currentGuess);
			} catch (GuessAlreadyMadeException e) {
				System.out.println("You already used that letter.");
				i--;
				continue;
			}

			System.out.print(currentGuess + "\n");

			guessedSoFar.add((String.valueOf(currentGuess)));			
			
			//figure out if they've guessed a correct letter
			//this happens in the set returned has words with the letter they guessed in it
			String firstWord = "";
			try {
				firstWord = possibleWords.iterator().next();
			} catch (NullPointerException e) {
				System.out.println("There are no words with that many letters. Try the game again with a different word length.");
				return;
			}

			int numberFound = 0;
			for (int k = 0; k < firstWord.length(); k++) {
				if (firstWord.charAt(k) == currentGuess) {
					word.deleteCharAt(k);
					word.insert(k, currentGuess);
					numberFound++;
					winner = true;
				}
			}
			if (numberFound == 0)
				System.out.println("Sorry, there are no " + currentGuess + "'s");
			else {
				i--;
				if (numberFound == 1)
					System.out.println("Yes, there is " + 1 + " " + currentGuess);
				else 
					System.out.println("Yes, there are " + numberFound + " " + currentGuess + "'s");
			}
			
			int totalReturnedWords = possibleWords.size();
			
			String wordString = word.toString();
			if (totalReturnedWords == 1 && winner && !wordString.contains("-")) {
				System.out.println("\nYou Win!");
				System.out.println("The correct word was " + possibleWords.iterator().next());
				winner = true;
				break;
			}
		}
		if (!winner) {
			System.out.println("\nYou lose! Try again next time.");
			System.out.println("The correct word was " + possibleWords.iterator().next());
		}
		guess.close();
	}
}
