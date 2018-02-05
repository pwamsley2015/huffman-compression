package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import huffman_tree.Frequency;
import huffman_tree.HuffmanTree;

/** 
 * @author Patrick Wamsley
 */
public class CharacterHuffmanTree implements HuffmanTree<Character> {
	
	/**
	 * When a file is encoded, the tree itself needs to be saved as well so the file can also be decoded. 
	 * When a file is saved, it will be done so in the following format: <br>
	 * (huffman tree encoding) + {@code END_OF_TREE_ENCODING_CHAR} + (data encoding)
	 */
	public static final String END_OF_TREE_ENCODING = "~DnE~";
	
	/**
	 * Seperates encodings between different codewords/symbols 
	 */
	public static final String CODEWORD_SEPERATOR = ";"; 
	
	/**
	 * Seperates a symbol and it's encoding 
	 */
	public static final String SYMBOL_CODE_SEPERATOR = ":"; 
	
	/**
	 * A node in the tree, simply keeps track of this node's frequency and children
	 * @author Patrick Wamsley
	 */
	private static class Node implements Comparable<Node> {
		private Frequency<Character> freq;
		private Node leftChild, rightChild, parent; 

		private Node(Frequency<Character> freq) {
			this.freq = freq; 
		}

		private void setLeftChild(Node child) {
			this.leftChild = child; 
			child.parent = this; 
		}

		private void setRightChild(Node child) {
			this.rightChild = child;
			child.parent = this; 
		}

		private Frequency<Character> getFrequency() {
			return freq; 
		}

		private Node getLeftChild() {
			return leftChild; 
		}

		private Node getRightChild() {
			return rightChild; 
		}

		private boolean isLeaf() {
			return leftChild == null && rightChild == null; 
		}
		
		private boolean isDummy() {
			return freq.object == null; 
		}

		@Override
		public int compareTo(Node o) {
			return freq.compareTo(o.freq); 
		}

		public Node getParent() {
			return parent; 
		}
		
		@Override
		public String toString() {
			return freq.toString(); 
		}
	}

	/**
	 * This tree's root node  	 
	 */
	private Node root; 
	
	/**
	 * Keeps a mapping from a character to it's Node in the tree. 
	 * Used for easy node finding. 
	 */
	private HashMap<Character, Node> lookUpTable = new HashMap<>(); 

	/**
	 * Creates an empty {@code CharacterHuffmanTree}
	 */
	public CharacterHuffmanTree() {}

	/**
	 * Creates an {@code CharacterHuffmanTree} with the initial character list 
	 */
	public CharacterHuffmanTree(List<Frequency<Character>> initList) {
		createFromFrequencies(initList); 
	}

	@Override
	public void createFromFrequencies(List<Frequency<Character>> frequencies) {

		//create a list of nodes
		ArrayList<Node> unaddedNodes = new ArrayList<>(); 
		for (Frequency<Character> f : frequencies) {
			Node node = new Node(f); 
			unaddedNodes.add(node); 
			lookUpTable.put(f.object, node); 
		}
		
		while (unaddedNodes.size() > 1) {
			Collections.sort(unaddedNodes); 
			
			Node f1 = unaddedNodes.get(0); 
			Node f2 = unaddedNodes.get(1); 

			int freqSum = f1.getFrequency().frequency + f2.getFrequency().frequency; 
			Frequency<Character> dummyFreq = new Frequency<Character>(null, freqSum); 
			Node parent = new Node(dummyFreq); 

			parent.setLeftChild(f1);
			parent.setRightChild(f2); 

			unaddedNodes.remove(f1);
			unaddedNodes.remove(f2);
			
			//add their summed freq back to the list
			unaddedNodes.add(parent);  
		
		}
		root = unaddedNodes.get(0); 
	}
	
	@Override
	public String getTreeEncoding() {
		return getCodeList() + END_OF_TREE_ENCODING; 
	}

	@Override
	public String getCodeList() {
		StringBuilder builder = new StringBuilder(); 
		for (Character c : lookUpTable.keySet()) {
			builder.append(c); 
			builder.append(SYMBOL_CODE_SEPERATOR); 
			builder.append(getBitString(c)); 
			builder.append(CODEWORD_SEPERATOR); 
		}
		return builder.toString(); 
	}

	@Override
	public String getBitString(Character e) {
		
		Node child = lookUpTable.get(e); 
		StringBuilder code = new StringBuilder(); 
		Node parent = child.getParent(); 
		
		while (parent != null) {
			if (parent.getLeftChild() == child) { //== is fine here since we're always working with the same nodes 
				code.append(0); 
			} else {
				code.append(1); 
			}
			child = parent; 
			parent = child.getParent(); 
		}
		
		//need to reverse the string if we care about convention 
		return code.reverse().toString(); 
	}

}
