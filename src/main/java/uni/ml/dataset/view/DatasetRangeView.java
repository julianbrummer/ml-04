package uni.ml.dataset.view;

import uni.ml.dataset.Sampling;

public class DatasetRangeView extends DatasetIndexedView {

	/**
	 * Creates a ranged view on a dataset. 
	 * @param baseView The dataset to decorate with this view.
	 * @param fromIndex The index of the first instance to incorporate into the view (inclusive).
	 * @param toIndex The end index. (exclusive)
	 */
	public DatasetRangeView(DatasetView baseView, int fromIndex, int toIndex) {
		super(baseView, Sampling.rangeArray(fromIndex, toIndex));
	}

}
