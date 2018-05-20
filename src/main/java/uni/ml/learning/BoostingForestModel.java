package uni.ml.learning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;
import uni.ml.tree.Classifier;

@RequiredArgsConstructor
public class BoostingForestModel extends DecisionModel implements Iterable<DecisionTreeModel> {
	
	private List<DecisionTreeModel> models = new ArrayList<>();
	private final int numIterations, maxDepth;

	
	/**
	 * To classify an instance of a dataset with a decision forest. 
	 * @author Julian Brummer
	 *
	 */
	private class ForestClassifier implements Classifier {
		
		@Override
		public Value<?> classify(Instance instance, EnumAttribute<?> classAttribute) {
			return Boosting.classification(models, instance, classAttribute);
		}
	}

	public void add(DecisionTreeModel model) {
		models.add(model);
	}
	
	public boolean hasModels() {
		return numModels() > 0;
	}
	
	public int numModels() {
		return models.size(); 
	}
	
	@Override
	public Iterator<DecisionTreeModel> iterator() {
		return models.iterator();
	}

	@Override
	public void trainModel(DatasetView examples, EnumAttribute<?> classAttribute) {
		models.clear();
		models.addAll(Boosting.modelGeneration(examples, numIterations, classAttribute, maxDepth));
	}

	@Override
	public Classifier classifier() {
		return new ForestClassifier();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (DecisionTreeModel model : models) {
			b.append(model).append("\n");
		}
		return b.toString();
	}
	
}
