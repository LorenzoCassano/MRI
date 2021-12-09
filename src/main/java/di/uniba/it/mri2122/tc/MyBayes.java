package di.uniba.it.mri2122.tc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import di.uniba.it.mri2122.entity.BoW;
import di.uniba.it.mri2122.entity.BoWUtils;


public class MyBayes extends TextCategorization{
	
	private HashMap<String,Float> priorprobs = new HashMap<>(); // prior probabilities
	private HashMap<String,BoW> postprob = new HashMap<>(); // Bow for each category
	private HashMap<WordCategory,Float> wordCategory = new HashMap<>(); // number of words for each category
	private HashSet<String> vocabulary = new HashSet<>();
	
	@Override
	public void train (List<DatasetExample> trainingset) throws IOException {
		HashMap<String,Integer> counter = new HashMap<>(); // count how much examples of categories are in the training set
		for(DatasetExample e: trainingset){
			if(!counter.containsKey(e.getCategory())){
				counter.put(e.getCategory(),1); // setting new category
				postprob.put(e.getCategory(),e.getBow());
			}else {
				counter.put(e.getCategory(),counter.get(e.getCategory()) + 1); // increasing
				postprob.put(e.getCategory(),BoWUtils.add(postprob.get(e.getCategory()),e.getBow()));
			}
		}
		for(String category : counter.keySet()) {
			priorprobs.put(category, (float) (counter.get(category)/counter.size()));
		}
		// calculating number of words for each category
		int i = 0;
		for (String category: postprob.keySet()){
			for(String word: postprob.get(category).getWords()){
				WordCategory w = new WordCategory(category,word);
				if(!wordCategory.containsKey(w)) {
					wordCategory.put(w,1 + postprob.get(category).getWeight(word)); // lapalce correction
				}else{
					wordCategory.put(w,wordCategory.get(w) + postprob.get(category).getWeight(word));
				}
				vocabulary.add(word); // filling vocabulary
				i++;
				if(i % 10000 == 0) System.out.println("Training ..." + i);
			}
		}
	}
	
	@Override
	public List<String> test (List<DatasetExample> testingset) throws IOException {
		List<String> test = new ArrayList<>(testingset.size());
		// filling vocabulary
		
		for(DatasetExample e: testingset){
			for(String word: e.getBow().getWords()){
				vocabulary.add(word);
			}
		}
		
		int size = vocabulary.size(); // size of vocabulary
		int i = 0;
		for(DatasetExample e: testingset) {
			Double max = null;
			String categoryMax = "";
			// calculating category
			for(String category : postprob.keySet()){
				double value = 0;
				String actualCategory = category;
				for(String word : e.getBow().getWords()) {
					WordCategory w = new WordCategory(category,word);
					if(wordCategory.containsKey(w)){
						value += Math.log(wordCategory.get(w)/(postprob.get(category).size() + (double)size)) * (double)e.getBow().getWeight(word);
					} else { // Laplace correction
						value += Math.log(1 / (postprob.get(category).size() + (double)size)) * (double) e.getBow().getWeight(word);
					}
				}
				if(max == null || value > max) {
					max = value;
					categoryMax = actualCategory;
				}
			}
			test.add(categoryMax);
			i++;
			if(i % 1000 == 0) System.out.println("Testing..." + i);
		}
		return test;
	}
}
