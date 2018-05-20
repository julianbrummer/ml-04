/**
 * Package for Machine Learning exercises.
 * @author Julian Brummer
 * @author Alexander Petri
 */
package uni.ml.exercise;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uni.ml.dataset.Dataset;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.dataset.DatasetPredicateView;
import uni.ml.dataset.DatasetRangeView;
import uni.ml.dataset.DatasetShuffleView;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Sampling;
import uni.ml.dataset.Value;
import uni.ml.learning.ClassificationResult;
import uni.ml.learning.DecisionModel;
import uni.ml.learning.DecisionTreeModel;
import uni.ml.learning.Measures;

/**
 * The main class for Exercise04 Task03.
 * @author Julian Brummer
 *
 */
public class Exercise04Task03 {
	
	/**
	 * Groups instances of the dataset by the values of the classAttribute.
	 * @param dataset The dataset to partition.
	 * @param classAttribute The target attribute.
	 * @return The datasets partitioned by classAttribute.
	 */
	public static List<DatasetView> stratification(DatasetView dataset, EnumAttribute<?> classAttribute) {
		List<DatasetView> partition = new ArrayList<>();
		for (Value<?> v : classAttribute) {
			partition.add(DatasetPredicateView.selectInstances(dataset, classAttribute, v));
		}
		return partition;
	}
	
	/**
	 * Shuffles the instances within a dataset.
	 * @param dataset The dataset to shuffle.
	 * @return A shuffled view on the dataset.
	 */
	public static DatasetView shuffle(DatasetView dataset) {
		return new DatasetShuffleView(dataset);
	}
	
   /**
    * Extracts the training set from a dataset using cross
    * validation.
    *
    * @param dataset The dataset(-view) to select training set from.
    * @param foldIdy The index of the current fold.
    * @param numFold The number of folds to use for cross validation.
    */
	public static DatasetView trainCV(DatasetView dataset, int foldIndex, int numFolds) {
		if (numFolds == 0) 
			return dataset;
		
		List<Integer> indices = Sampling.rangeList(0, foldIndex * dataset.numInstances()/numFolds);
		indices.addAll(Sampling.rangeList((foldIndex+1) * (dataset.numInstances()/numFolds), dataset.numInstances()));
		return new DatasetIndexedView(dataset, indices);
	}
	
   /**
    * Extracts the test set from a dataset using cross
    * validation.
    *
    * @param dataset The dataset(-view) to select test set from.
    * @param foldIdy The index of the current fold.
    * @param numFold The number of folds to use for cross validation.
    */
	public static DatasetView testCV(DatasetView dataset, int foldIndex, int numFolds) {
		if (numFolds == 0) 
			return dataset;
	
		return new DatasetRangeView(dataset, foldIndex * dataset.numInstances()/numFolds, (foldIndex+1) * (dataset.numInstances()/numFolds));
	}	
	
	/**
	 * Evaluates a decision model using stratified cross validation.
	 * @param dataset The full dataset.
	 * @param classAttribute The target attribute.
	 * @param model The decision model to evaluate.
	 * @param numFolds The number of cross validation folds.
	 * @return The mean and standard deviation of the accuracy.
	 */
	public static ClassificationResult stratifiedCrossValidation(DatasetView dataset, EnumAttribute<?> classAttribute, DecisionModel model, int numFolds) {
		List<Float> accuracy = new ArrayList<>();
		
		for (int i = 0; i < numFolds; i++) {
			model.trainModel(trainCV(dataset, i, numFolds), classAttribute);
			accuracy.add(model.testModel(testCV(dataset, i, numFolds), classAttribute));
		}
		
		return Measures.meanDev(accuracy);
	}


	public static void main(String[] args) {
		Dataset dataset = new Dataset();
		if (args.length > 0) {
			try {
				dataset.loadFromFile(new File(args[0]));
				
				int maxDepth = Integer.parseInt(args[1]);
				int numFolds = Integer.parseInt(args[2]);

				ClassificationResult accuracy = stratifiedCrossValidation(dataset, dataset.lastAttribute(), new DecisionTreeModel(maxDepth), numFolds);
				
				System.out.println("Dataset: " + dataset.name());
				System.out.println("Number of Folds: " + numFolds);
				System.out.println("MaxDepth: " + maxDepth);
				System.out.println("Accuracy: " + accuracy);
			
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
		
	}

}
