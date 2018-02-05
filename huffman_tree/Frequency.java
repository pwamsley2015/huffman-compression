package huffman_tree;

/**
 * An object wrapper class which also stores the frequency of this object. 
 * 
 * @author Patrick Wamsley
 */
public class Frequency<T> implements Comparable<Frequency<T>> {
	
	public final int frequency; 
	public final T object;  
	
	public Frequency(T object, int frequency) {
		 this.object = object; 
		 this.frequency = frequency; 
	}

	@Override
	public int compareTo(Frequency<T> o) {
		return this.frequency - o.frequency; 
	}
	
	@Override
	public String toString() {
		return String.valueOf(object) + "freq = " + frequency; 
	}

}
