package di.uniba.it.mri2122.tc;

import java.util.HashMap;
import java.util.Objects;

public class WordCategory implements Comparable{
	private String category;
	private String word;
	
	public WordCategory(String category,String word){
		this.category = category;
		this.word = word;
	}
	
	public WordCategory () {
		category = new String();
		word = new String();
	}
	
	public String getCategory () {
		return category;
	}
	
	public String getWord () {
		return word;
	}
	
	public void setCategory (String category) {
		this.category = category;
	}
	
	public void setWord (String word) {
		this.word = word;
	}
	
	@Override
	public boolean equals (Object o) {
		WordCategory e = (WordCategory) o;
		return this.category.equals(e.getCategory()) && this.word.equals(e.getWord());
	}
	
	@Override
	public int compareTo (Object o) {
		WordCategory e = (WordCategory) o;
		if(category.equals(e.getCategory()) && word.equals(e.getWord())) return 0;
		return -1;
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(getCategory(), getWord());
	}
	
	public String toString(){
		return "category: " + category + ", word: " + word;
	}
	
	// class tester
	public static void main(String[] args) {
		HashMap<WordCategory,Float> a = new HashMap<>();
		a.put(new WordCategory("abc","ccc"), 10.F);
		System.out.println(a);
		a.put(new WordCategory("abc","aaa"),10.F);
		System.out.println(a);
		System.out.println(a.get(new WordCategory("abc","aaa")));
		System.out.println(a.get(new WordCategory("abc","aaa")));
		a.put(new WordCategory("abc","aaa"),100.F);
		System.out.println(a);
	}
}
