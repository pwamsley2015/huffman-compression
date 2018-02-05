package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import huffman_tree.Frequency; 

/**
 * @author Patrick Wamsley
 */
public class HuffmanTextCodec {

	/**
	 * Entry point of the program. Parses command line arguements, then either decodes or encodes a huffman file. 
	 * 
	 * Program must be called from the command line in the following format: <br>
	 * --input inputFile --process encode/decode --output outputFile
	 * 
	 * @see README.txt
	 */
	public static void main(String[] args) {

		if (args.length != 6) {
			error(); 
		}

		if (args[3].equalsIgnoreCase("encode")) {
			try {
				encode(args[1], args[5]);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else if (args[3].equalsIgnoreCase("decode")) {
			try {
				decode(args[1], args[5]);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else {
			error(); 
		}

	}

	/**
	 * Encodes the file contents of {@code inputFilePath} into a compressed huff file at {@code outputFilePath}
	 */
	private static void encode(String inputFilePath, String outputFilePath) throws IOException {
		String fileContents = getFileContents(inputFilePath);

		if (fileContents.length() == 0) {
			System.err.println("Input File was empty. There's nothing to encode. Please try again.");
			System.exit(0); 
		}

		CharacterHuffmanTree tree = new CharacterHuffmanTree(getCharacterFrequencies(fileContents)); 
		StringBuilder encoding = new StringBuilder(); 
		
		for (char c : fileContents.toCharArray()) {
			encoding.append(tree.getBitString(c)); 
		}

		//now just write to the output file
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath)); 
		writer.write(tree.getTreeEncoding() + encoding.toString());
		writer.close(); 
	}

	/**
	 * Writes the contents of the decoded huff file at {@code inputFilePath} to {@code outputFilePath}
	 * @throws IOException 
	 */
	private static void decode(String inputFilePath, String outputFilePath) throws IOException {
		String fileContents = getFileContents(inputFilePath); 
		if (fileContents.length() == 0) {
			System.err.println("Input File was empty. There's nothing to decode. Please try again.");
			System.exit(0); 
		}

		String[] fileSections = fileContents.split(CharacterHuffmanTree.END_OF_TREE_ENCODING); 
		String treeEncoding = fileSections[0]; 
		String dataEncoding = fileSections[1]; 	

		/*
		 * First we need to recover the tree from the file so we can do decodings. 
		 * We'll save the codes to a hashmap for easy look up and decoding
		 */
		HashMap<String, Character> codes = restoreTreeEncodings(treeEncoding); 

		/*
		 * Now we just have to parse the dataEncoding and restore the orginal file
		 */
		String currentPossibleEncoding = ""; 

		StringBuilder decoding = new StringBuilder(); 

		for (char charInEncoding : dataEncoding.toCharArray()) {
			Character symbol = codes.get(currentPossibleEncoding); 
			if (symbol != null) {
				decoding.append(symbol); 
				currentPossibleEncoding = ""; 
			} 				
			currentPossibleEncoding += charInEncoding; 
		}

		//now we just need to write the decoded message to the output file
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath)); 
		writer.write(decoding.toString());
		writer.close(); 
	}

	/**
	 * Restores the tree encodings from the huff file to be used for decoding. 
	 * 
	 * @param treeEncoding - the tree encoding section of the input file 
	 * @return a {@code HashMap} mapping codes to their characters
	 */
	private static HashMap<String, Character> restoreTreeEncodings(String treeEncoding) {
		HashMap<String, Character> codes = new HashMap<>(); 

		String[] encodings = treeEncoding.split(CharacterHuffmanTree.CODEWORD_SEPERATOR); 

		for (String encoding : encodings) {
			//the first char will always be the symbol, followed by ":<encoding>"
			codes.put(encoding.substring(2), encoding.charAt(0)); 
		}

		return codes; 
	}

	/**
	 * Creates the Frequency list from the raw String. 
	 */
	private static List<Frequency<Character>> getCharacterFrequencies(String raw) {

		HashMap<Character, Integer> histogram = new HashMap<>(); 

		for (char c : raw.toCharArray()) {
			Integer count = histogram.get(c); 
			if (count == null) {
				histogram.put(c, 1); 
			} else {
				histogram.put(c, count + 1); 
			}
		}

		List<Frequency<Character>> returnList = new ArrayList<>();

		for (Character c : histogram.keySet()) {
			returnList.add(new Frequency<Character>(c, histogram.get(c))); 
		}

		return returnList; 
	}

	/**
	 * Reads a file and returns the file contents as a String
	 */
	private static String getFileContents(String inputFilePath) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath)); 

		StringBuilder contents = new StringBuilder(); 
		String currLine = reader.readLine(); 

		while (currLine != null) {
			contents.append(currLine); 
			contents.append("\n"); 
			currLine = reader.readLine(); 
		}
		
		reader.close(); 
		return contents.toString(); 
	}

	private static void error() {
		System.err.println("Program run incorrectly. Must be ran with the following command line arguements:"); 
		System.err.println("--input <inputFile> --process <encode/decode> --output <outputFile>");
		System.err.println("Please try again with correctly formatted command line arguements."); 
		System.exit(0);
	}
}
