package uni.ml.learning;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.dataset.DatasetSplit;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;
import uni.ml.tree.Classifier;

@Accessors(fluent=true)
public abstract class DecisionModel {
	
	@Getter
	private float error;
	

	
	/**
	 * Trains the decision model with the provided examples.
	 * @param examples The dataset to create the decision model from.
	 * @param classAttribute The classification/target attribute.
	 */
	public abstract void trainModel(DatasetView examples, EnumAttribute<?> classAttribute);
	
	/**
	 * @return The classifier of this model.
	 */
	public abstract Classifier classifier();
	
	/**
	 * Classifies an instance.
	 * @return The value predicted by this model for the instance.
	 */
	public Value<?> classify(Instance instance, EnumAttribute<?> classAttribute) {
		return classifier().classify(instance, classAttribute);
	}	
	
	/**
	 * Trains the decision model with a subset of an example set.
	 * @param examples The dataset to create the decision tree from.
	 * @param indices Specifies a subset of the dataset by selecting instances (rows) through indices.
	 * @param classAttribute The classification/target attribute. 
	 */
	public void trainModelOnSubset(DatasetView examples, int[] indices, EnumAttribute<?> classAttribute) {
		trainModel(new DatasetIndexedView(examples, indices), classAttribute);
	}
	
	/**
	 * Tests the model with a subset of a dataset.
	 * @param dataset The dataset to select a test set from.
	 * @param indices Specifies a subset of the dataset by selecting instances (rows) through indices.
	 * @return The percantage of correctly classified instances.
	 */
	public float testModelOnSubset(DatasetView dataset, int[] indices, EnumAttribute<?> classAttribute) {
		return testModel(new DatasetIndexedView(dataset, indices), classAttribute);
	}
	
	/**
	 * Tests the model with a test dataset.
	 * This also updates the classification error of this model.
	 * @param testSet The dataset to test the model.
	 * @return The percentage of correctly classified instances.
	 * @see error()
	 */
	public float testModel(DatasetView testSet, EnumAttribute<?> classAttribute) {
		Classifier classifier = classifier();
		float correctlyClassified = 0.0f;
		
		for (Instance instance : testSet.instances()) {
			if (classifier.test(instance, classAttribute)) {
				correctlyClassified += 1.0f;
			}
		}
		correctlyClassified /= testSet.numInstances();
		error = 1.0f - correctlyClassified;
		return correctlyClassified;
	}
	
	/*	public float testModel(DatasetView testSet, EnumAttribute<?> classAttribute) {
	Classifier classifier = classifier();
	float correctlyClassified = 0.0f;
	float classified = 0.0f;
	
	for (Instance instance : testSet.instances()) {
		if (classifier.test(instance, classAttribute)) {
			correctlyClassified += instance.weight();
		}
		classified += instance.weight();
	}
	correctlyClassified /= classified;
	error = 1.0f - correctlyClassified;
	return correctlyClassified;
}*/
	
	/**
	 * Trains and tests a decision tree model a number of times.
	 * @param dataset The dataset to train and test the model with. The dataset is split randomly into a training and test set. 
	 * @param trainingRatio The ratio of the dataset to use for training.
	 * @param repeats The number of training and test cycles.
	 * @param classAttribute The target/classification attribute.
	 * @return The mean and standard deviation of correctly classified instances.
	 */
	public ClassificationResult trainAndTestModel(DatasetView dataset, float trainingRatio, int repeats, EnumAttribute<?> classAttribute) {
		List<Float> classification = new ArrayList<>();
		for (int i = 0; i < repeats; i++) {
			DatasetSplit split = dataset.randomSplit(trainingRatio);
			trainModel(split.trainingSet, classAttribute);
			classification.add(testModel(split.testSet, classAttribute));
		}
		return Measures.meanDev(classification);
	}
	
	/**
	 * Convenience method to print a decision model.
	 * @param root The root node of the tree to print.
	 */
	public void print() {
		System.out.println(toString());
	}

}
