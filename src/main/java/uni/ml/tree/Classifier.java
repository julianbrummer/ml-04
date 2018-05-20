package uni.ml.tree;

import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;

/**
 * Provides an interface for decision model classification. 
 * @author Julian Brummer
 *
 */
public interface Classifier {

	/**
	 * Classifies an instance with this classifier.
	 * @param classAttribute The target/classification attribute.
	 * @return The value predicted by this classifier.
	 */
	public Value<?> classify(Instance instance, EnumAttribute<?> classAttribute);
	
	/**
	 * Tests whether the value predicted by this classifier is equal to the value of the test instance.
	 */
	default boolean test(Instance instance, EnumAttribute<?> classAttribute) {
		return instance.value(classAttribute).equals(classify(instance, classAttribute));
	}
}
