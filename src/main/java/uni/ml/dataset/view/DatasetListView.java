package uni.ml.dataset.view;

import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;

/**
 * A dataset view that links the instances of multiple views.
 * The datasets are required to have the same attributes.
 * @author Julian Brummer
 *
 */
@NoArgsConstructor
public class DatasetListView extends DatasetView {

	private List<DatasetView> datasets = new ArrayList<>();
	private int numInstances;
	
	public DatasetListView(List<DatasetView> datasets) {
		this.datasets = datasets;
		for (DatasetView dataset : datasets) {
			numInstances += dataset.numInstances();
		}
	}
	
	public void append(DatasetView dataset) {
		datasets.add(dataset);
		numInstances += dataset.numInstances();
	}
	
	@Override
	public int numAttributes() {
		return datasets.isEmpty()? 0 : datasets.get(0).numAttributes();
	}

	@Override
	public int numInstances() {
		return numInstances;
	}

	@Override
	public EnumAttribute<?> attributeAt(int index) {
		return datasets.isEmpty()? null : datasets.get(0).attributeAt(index);
	}

	@Override
	public Instance instanceAt(int index) {
		for (DatasetView dataset : datasets) {
			if (index < dataset.numInstances())
				return dataset.instanceAt(index);
			index -= dataset.numInstances();
		}
		return null;
	}

}
