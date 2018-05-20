package uni.ml.dataset.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import uni.ml.dataset.Attribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;

/**
 * A view on a dataset, that only includes instances that comply with a given predicate.
 * @author Julian Brummer
 *
 */
public class DatasetPredicateView extends DatasetIndexedView {

	/**
	 * Computes an index list of instances that comply with the specified predicate.
	 * @param baseView The dataset(-view) to search for instances.
	 * @param predicate The predicate to test for each instance.
	 */
	private static List<Integer> validIndices(DatasetView baseView, Predicate<Instance> predicate) {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < baseView.numInstances(); i++) {
			if (predicate.test(baseView.instanceAt(i))) {
				indices.add(i);
			}
		}
		return indices;
	}
	
	/**
	 * Decorates the given dataset(-view) with a predicate to filter instances.
	 */
	public DatasetPredicateView(DatasetView baseView, Predicate<Instance> predicate) {
		super(baseView, validIndices(baseView, predicate));
	}
	
	/**
	 * Selects all instances within the provided dataset(-view) which have the specified attribute-value pair.
	 * @param dataset The dataset(-view) to create the subset from.
	 * @return the subset view on the dataset.
	 */
	public static DatasetPredicateView selectInstances(DatasetView dataset, Attribute<?> attribute, Value<?> value) {
		return new DatasetPredicateView(dataset, (instance) -> value.equals(instance.value(attribute)));
	}

}
