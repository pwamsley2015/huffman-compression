package huffman_tree;

import java.util.List;

/**
 * A basic HuffmanTree interface.
 * 
 * @author Ritwik Banerjee 
 * @author Patrick Wamsley
 */
public interface HuffmanTree<T> {
	/**
	 * Rebuilds this HuffmanTree from a given list of character frequencies. This is
	 * a destructive operation, i.e., it will override any previous elements in the
	 * tree. This method can be used as a helper method in a constructor.
	 *
	 * @param frequencies A list of Frequency objects.
	 */
	public void createFromFrequencies(List<Frequency<T>> frequencies);
	
	/**
	 * Generates a String to save the encodings made by the tree. Used to save the 
	 * structure of the tree so files can be decoded. 
	 * 
	 * @see getBitString() 
	 */
	public String getTreeEncoding();
	
	/**
	 * Returns a String representation of the tree’s list of elements (in String
	 * representation) and their bit string codes. The String should be a comma
	 * separated list of elements and their codes, on a single line. Elements and codes
	 * are separated by colons. E.g., ‘‘l:000,s:001,o:01,g:10, :110,e:1110,r:1111’’.
	 *
	 * @return A String list of the tree elements and their associated bit codes
	 */
	public String getCodeList();
	
	/**
	 * Returns the trees’ bit string representation for the specified item, enabling
	 * a look-up of the code for an element.
	 *
	 * @param e The element to look up the code of
	 * @return A bit string corresponding to the specified element, or null if
	 * the element is not in the tree.
	 */
	public String getBitString(T e);
}
