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
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Sampling;
import uni.ml.dataset.Value;
import uni.ml.dataset.view.DatasetIndexedView;
import uni.ml.dataset.view.DatasetListView;
import uni.ml.dataset.view.DatasetPredicateView;
import uni.ml.dataset.view.DatasetRangeView;
import uni.ml.dataset.view.DatasetShuffleView;
import uni.ml.dataset.view.DatasetView;
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
	 * Creates and saves datasets for stratified cross validation.
	 * @param dataset The full dataset.
	 * @param classAttribute The target attribute.
	 * @param numFolds The number of cross validation folds.
	 * @param directory Specifies the folder to which the training and testSet created during cross validation should be saved,
	 * or <code>null</code> to not save.
	 */
	public static void createSCVFiles(DatasetView dataset, EnumAttribute<?> classAttribute, int numFolds, File directory) {
		// split dataset by values of classAttribute and shuffle each one
		List<DatasetView> stratified = stratification(dataset, classAttribute);
		for (int i = 0; i < stratified.size(); i++) {
			stratified.set(i, shuffle(stratified.get(i)));
		}
		
		
		for (int i = 0; i < numFolds; i++) {
			// from each same-classed-dataset select a training- and testset (ratio numFolds:1) according to cross validation
			// these sets are then linked together to form the final stratified training- and testset.
			DatasetListView trainingSet = new DatasetListView();
			DatasetListView testSet = new DatasetListView();
			for (int j = 0; j < stratified.size(); j++) {
				trainingSet.append(trainCV(stratified.get(j), i, numFolds)); 
				testSet.append(testCV(stratified.get(j), i, numFolds));
			}
			
			// save sets to specified directory
			if (directory != null) {
				trainingSet.name("trainingSet"+i);
				testSet.name("testSet"+i);
				try {
					trainingSet.saveToArff(directory);
					testSet.saveToArff(directory);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Evaluates a decision model using stratified cross validation.
	 * @param dataset The full dataset.
	 * @param classAttribute The target attribute.
	 * @param model The decision model to evaluate.
	 * @param numFolds The number of cross validation folds.
	 * @param directory Specifies the folder to which the training and testSet created during cross validation should be saved,
	 * or <code>null</code> to not save.
	 * @return The mean and standard deviation of the accuracy.
	 */
	public static ClassificationResult stratifiedCrossValidation(DatasetView dataset, EnumAttribute<?> classAttribute, DecisionModel model, 
			int numFolds, File directory) {
		List<Float> accuracy = new ArrayList<>(); //stores the classification accuracies for each test run
		
		// split dataset by values of classAttribute and shuffle each one
		List<DatasetView> stratified = stratification(dataset, classAttribute);
		for (int i = 0; i < stratified.size(); i++) {
			stratified.set(i, shuffle(stratified.get(i)));
		}
		
		
		for (int i = 0; i < numFolds; i++) {
			// from each same-classed-dataset select a training- and testset (ratio numFolds:1) according to cross validation
			// these sets are then linked together to form the final stratified training- and testset.
			DatasetListView trainingSet = new DatasetListView();
			DatasetListView testSet = new DatasetListView();
			for (int j = 0; j < stratified.size(); j++) {
				trainingSet.append(trainCV(stratified.get(j), i, numFolds)); 
				testSet.append(testCV(stratified.get(j), i, numFolds));
			}
			
			// save sets to specified directory
			if (directory != null) {
				trainingSet.name("trainingSet"+i);
				testSet.name("testSet"+i);
				try {
					trainingSet.saveToArff(directory);
					testSet.saveToArff(directory);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// train and test
			model.trainModel(trainingSet, classAttribute);
			accuracy.add(model.testModel(testSet, classAttribute));
		}
		
		return Measures.meanDev(accuracy); // compute mean and standard deviation
	}


	public static void main(String[] args) {
		Dataset dataset = new Dataset();
		if (args.length > 0) {
			try {
				dataset.loadFromFile(new File(args[0]));
				System.out.println("Dataset: " + dataset.name());
				
				int maxDepth = Integer.parseInt(args[1]);
				int numFolds = Integer.parseInt(args[2]);
				File outputPath = args.length >= 4? new File(args[3]) : null;
				
				if (maxDepth <= 0) { // do not run scv just create files
					createSCVFiles(dataset, dataset.lastAttribute(), numFolds, outputPath);
				} else {
					ClassificationResult accuracy = stratifiedCrossValidation(dataset, dataset.lastAttribute(), 
																			  new DecisionTreeModel(maxDepth), 
																			  numFolds, outputPath);
										
					System.out.println("Number of instances: " + dataset.numInstances());
					System.out.println("Number of Folds: " + numFolds);
					System.out.println("MaxDepth: " + maxDepth);
					System.out.println("Accuracy: " + accuracy);
				}
			} catch (IOException e) {
				e.printStackTrace();				
			}
		}
		
	}

}
