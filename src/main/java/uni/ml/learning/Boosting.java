package uni.ml.learning;

import java.util.ArrayList;
import java.util.List;

import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;
import uni.ml.dataset.WeightedValues;
import uni.ml.dataset.view.DatasetView;
import uni.ml.tree.Classifier;

public class Boosting {
	
	public static List<DecisionTreeModel> modelGeneration(DatasetView dataset, int numIterations, EnumAttribute<?> classAttribute, int maxDepth) {
		List<DecisionTreeModel> models = new ArrayList<>();
		dataset.assignEqualWeights();
		for (int i = 0; i < numIterations; i++) {
			DatasetView sampledSet = dataset.weightedBootstrapSampling();
			// train and test model on same sampled dataset
			DecisionTreeModel model = new DecisionTreeModel(maxDepth);
			model.trainModel(sampledSet, classAttribute);
			model.testModel(sampledSet, classAttribute);
			float e = model.error();
			
			//System.out.println(sampledSet);
			//System.out.println(model);
			//System.out.println("Error " + e);
			//System.out.println("\n");
			
			// abort if error exceeds 0.5
			if (e >= 0.5f) {
				break;
			}
			models.add(model); // store valid model
			if (e == 0.0f) {
				break;
			}
			// recompute and normalize weights
			Classifier classifier = model.classifier();
			for (Instance instance : dataset.instances()) {
				if (classifier.test(instance, classAttribute))
					instance.multiplyWeight(e/(1-e));
			}
			dataset.normalizeWeights();
		}
		return models;
	}
	
	
	public static Value<?> classification(List<DecisionTreeModel> models, Instance instance, EnumAttribute<?> classAttribute) {
		if (models.isEmpty())
			return classAttribute.values().next();
		
		WeightedValues values = new WeightedValues(classAttribute);
		for (DecisionTreeModel model : models) {
			float e = model.error();
			Value<?> classValue = model.classify(instance, classAttribute);
			values.applyToWeight(classValue, (float) -Math.log(e/(1-e)));
		}
		
		return values.maxWeightedValue();
	}
	
}
