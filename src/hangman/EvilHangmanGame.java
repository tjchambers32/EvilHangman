package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame {

	Set<String> dictionary;
	Map<String, HashSet<String>> possibleWords;
	Set<String> guessedSoFar;
	private int wordLength;
	private char guess;

	public EvilHangmanGame() {
		dictionary = new HashSet<String>();
		possibleWords = new HashMap<String, HashSet<String>>();
		guessedSoFar = new TreeSet<String>();
		wordLength = 0;
	}

	@Override
	public void startGame(File dictionaryInput, int wordLength) {
		dictionary.clear();
		Scanner scanner = null;

		try {
			scanner = new Scanner(dictionaryInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (scanner.hasNext()) {
			String nextWord = scanner.next();
			if (nextWord.length() == wordLength)
				dictionary.add(nextWord);
		}

		this.wordLength = wordLength;
	}

	private String makeGroupKey(String word) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (c == guess)
				sb.append(guess);
			else
				sb.append('-');
		}

		return sb.toString();
	}

	private Set<String> findLargestPartition() {

int largestSetSize = 0;
		HashSet<String> largestSet = new HashSet<String>();
		String largestKey = "";

		int tempSetSize = 0;
		HashSet<String> tempSet = new HashSet<String>();
		String tempKey;

		for (Map.Entry<String, HashSet<String>> partition : possibleWords
				.entrySet()) {

			tempSet = partition.getValue();
			tempSetSize = partition.getValue().size();
			tempKey = partition.getKey();

			if (tempSetSize > largestSetSize) {
				largestSetSize = tempSetSize;
				largestSet = tempSet;
				largestKey = tempKey;
			} else if (tempSetSize == largestSetSize) {
				String temp1 = tempKey.replace('-', ' ').trim();
				String temp2 = largestKey.replace('-', ' ').trim();
				if (temp2.length() > temp1.length()) {
					largestSet = tempSet;
					largestKey = tempKey;
				} else if (temp1.length() == temp2.length()) {
					for (int i = 1; i < wordLength; i++) {
						int index1 = tempKey.lastIndexOf(guess, wordLength-i);
						int index2 = largestKey.lastIndexOf(guess, wordLength-i);
						if (index1 > index2) {
							largestKey = tempKey;
							break;
						} else if (index2 > index1)
							break;
					}
				}
			}
		}
		largestSet = possibleWords.get(largestKey);
		return largestSet;
		
	}

	@Override
	public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
		// set is possible words

		if (guessedSoFar.contains(String.valueOf(guess))) {
			throw new GuessAlreadyMadeException();
		} else {
			guessedSoFar.add(String.valueOf(guess));
		}

		this.guess = guess;

		possibleWords = new HashMap<String, HashSet<String>>();
		
		for (String word : dictionary) {
			String groupKey = makeGroupKey(word);
			if (possibleWords.containsKey(groupKey))
				possibleWords.get(groupKey).add(word);
			else {
				HashSet<String> newSet = new HashSet<String>();
				newSet.add(word);
				possibleWords.put(groupKey, newSet);
			}
		}
		Set<String> returnSet = new HashSet<String>();
		returnSet = findLargestPartition();

		dictionary = returnSet;
		
		return dictionary;
	}
}
