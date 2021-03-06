package uni.ml.dataset.view;

import uni.ml.dataset.Sampling;

public class DatasetShuffleView extends DatasetIndexedView {

	/**
	 * Creates a shuffled view on the specified dataset.
	 * @param baseView The dataset(-view) to decorate with a shuffled view.
	 */
	public DatasetShuffleView(DatasetView baseView) {
		super(baseView, Sampling.shuffleArray(baseView.numInstances()));
	}

}
