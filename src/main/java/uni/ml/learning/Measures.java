package uni.ml.learning;

import java.util.List;

import uni.ml.dataset.Dataset;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.dataset.DatasetPredicateView;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Value;

/**
 * Provides some static methods to measure learning of decision trees.
 * @author Julian Brummer *
 */
public class Measures {
	
	/**
	 * Computes the ratio of instances with the specified key,value-pair within a dataset(-view). 
	 */
	public static float ratio(DatasetView dataset, EnumAttribute<?> attribute, Value<?> value) {
		if (!dataset.hasInstances()) {
			return 0.0f;
		}
		DatasetView valueSubset = DatasetPredicateView.selectInstances(dataset, attribute, value); // Sv
		return (float) valueSubset.numInstances()/dataset.numInstances(); 
	}
	
	/**
	 * Finds the most common value of the specified attribute within a dataset(-view). 
	 */
	public static Value<?> mostCommonValue(DatasetView dataset, EnumAttribute<?> attribute) {
		float maxRatio = Float.NEGATIVE_INFINITY;
		Value<?> mcv = null;
		for (Value<?> value : attribute) {
			float ratio = ratio(dataset, attribute, value); 
			if (ratio > maxRatio) {
				maxRatio = ratio;
				mcv = value;
			}				
		}
		return mcv;
	}
	
	/**
	 * Computes the entropy of a dataset(-view).
	 * @param dataset The dataset or a view on a subset.
	 * @param classAttribute The classification attribute with a fixed number of allowed string values.
	 */
	public static float entropy(DatasetView dataset, EnumAttribute<?> classAttribute) {	
		float entropy = 0.0f;
		for (Value<?> value : classAttribute) { // iterate over allowed values of the attribute C
			float pv = ratio(dataset, classAttribute, value);
			if (pv != 0) { // avoid log(0)
				entropy -= pv * Math.log(pv)/Math.log(2);
			}
		}
		return entropy;
	}
	
	/**
	 * Computes the entropy on a subset of a dataset.
	 * @param indices Specifies the data subset by a list of instance indices.
	 * @param classAttribute The classification attribute with a fixed number of allowed string values.
	 */
	public static float entropyOnSubset(Dataset dataset, int[] indices, EnumAttribute<?> classAttribute) {
		return entropy(new DatasetIndexedView(dataset, indices), classAttribute); // compute entropy from subset
	}
	
	/**
	 * Computes the information gain by splitting a dataset(-view) at the specified attribute.
	 * @param dataset The dataset or a view on a subset.
	 * @param classAttribute The classification attribute with a fixed number of allowed string values.
	 * @param splitAttribute The attribute by which the dataset is split to compute the information gain.
	 */
	public static float informationGain(
			DatasetView dataset, 
			EnumAttribute<?> classAttribute, 
			EnumAttribute<?> splitAttribute) {
		
		float gain = entropy(dataset, classAttribute);
		
		for (Value<?> value : splitAttribute) { // iterate over allowed values of the split-attribute A
			DatasetView valueSubset = DatasetPredicateView.selectInstances(dataset, splitAttribute, value); // Sv
			float weight = (float) valueSubset.numInstances()/dataset.numInstances(); // |Sv|/|S|
			gain -= weight * entropy(valueSubset, classAttribute);
		}
		return gain;
	}
	
	/**
	 * Computes the information gain by splitting a subset of a dataset at the specified attribute.
	 * @param indices Specifies the data subset by a list of instance indices.
	 * @param classAttribute The classification attribute with a fixed number of allowed string values.
	 * @param splitAttribute The attribute by which the subset is split to compute the information gain.
	 */
	public static float informationGain(
			Dataset dataset, 
			int[] indices, 
			EnumAttribute<?> classAttribute, 
			EnumAttribute<?> splitAttribute) {
		// compute information gain from subset S
		return informationGain(new DatasetIndexedView(dataset, indices), classAttribute, splitAttribute);
	}
	
	/**
	 * Computes the mean and standard deviation from a list of classification results.
	 */
	public static ClassificationResult meanDev(List<Float> accuracy) {
		float meanClassified = 0.0f;
		float deviationClassified = 0.0f;
		int n = accuracy.size();
		for (int i = 0; i < n; i++) {
			meanClassified += accuracy.get(i);
		}
		meanClassified /= n;
		for (int i = 0; i < n; i++) {
			deviationClassified += Math.pow(accuracy.get(i)-meanClassified, 2);
		}
		deviationClassified /= n;
		deviationClassified = (float) Math.sqrt(deviationClassified);
		return new ClassificationResult(meanClassified, deviationClassified);
	}
}
