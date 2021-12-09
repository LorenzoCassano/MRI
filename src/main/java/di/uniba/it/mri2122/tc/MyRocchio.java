package di.uniba.it.mri2122.tc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import di.uniba.it.mri2122.entity.BoW;
import di.uniba.it.mri2122.entity.BoWUtils;

public class MyRocchio extends TextCategorization{
	
	private HashMap<String,BoW> centroids = new HashMap<>();
	
	private float alpha = 0.8f;
	
	private float beta = 0.2f;
	
	@Override
	public void train (List<DatasetExample> trainingset) throws IOException {
		/*
		This training sets only positive feedback
		For setting negative feedback we need to do others loop for each category
		 */
		HashMap<String,BoW> toUse = new HashMap<>();
		HashMap<String,Integer> counter = new HashMap<>(); // counting number of categories
		//Sum of words' weight
		int i = 0;
		for (DatasetExample e : trainingset) { // updating weight of word for each category
			if(!toUse.containsKey(e.getCategory())){
				toUse.put(e.getCategory(),e.getBow());
				counter.put(e.getCategory(),1);
			} else {
				// adding Bow
				toUse.put(e.getCategory(), BoWUtils.add(toUse.get(e.getCategory()), e.getBow()));
			}
			i++;
			if(i % 1000 == 0) System.out.println("MyRocchio summing vectors... " + i);
		}
		BoW negativeFeedBack = new BoW();
		for(String category: toUse.keySet()){
			BoW positiveFeedback = toUse.get(category);
			// negative Feedback
			for(String c: toUse.keySet()){
				if(!c.equals(category)){
					BoWUtils.add(negativeFeedBack,toUse.get(c));
				}
			}
			int number = counter.get(category);
			BoWUtils.scalarProduct(alpha/number,positiveFeedback);
			BoWUtils.scalarProduct(-beta/(counter.size()-number),negativeFeedBack);
			centroids.put(category,BoWUtils.add(positiveFeedback,negativeFeedBack));
		}
	}
	
	@Override
	public List<String> test (List<DatasetExample> testingset) throws IOException {
		List<String> test = new ArrayList<>(testingset.size());
		int i = 0;
		for (DatasetExample e : testingset) {
			float max = 0;
			String categoryMax = "";
			for (String category: centroids.keySet()) {
				float value = BoWUtils.sim(centroids.get(category),e.getBow());
				if(value > max) {
					max = value;
					categoryMax = category;
				}
			}
			test.add(categoryMax);
			i++;
			if(i % 1000 == 0) System.out.println("MyRocchio testing... " + i);
		}
		return test;
	}
	
}
