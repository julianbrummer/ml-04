package uni.ml.dataset;

import lombok.AllArgsConstructor;

/**
 * Represents a training and a test set of a dataset.
 * @author Julian Brummer
 *
 */
@AllArgsConstructor
public class DatasetSplit {
	public DatasetView trainingSet;
	public DatasetView testSet;
}
