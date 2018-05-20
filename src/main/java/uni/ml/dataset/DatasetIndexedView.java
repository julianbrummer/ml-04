package uni.ml.dataset;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
/**
 * A view on a dataset, that only includes instances with specific (row) indices.
 * @author Julian Brummer
 *
 */
@AllArgsConstructor
public class DatasetIndexedView extends DatasetView {

	private DatasetView baseView;
	private List<Integer> indices;
	
	/**
	 * Decorates the given dataset(-view) with an index list to select instances.
	 */
	public DatasetIndexedView(DatasetView baseView, int... indices) {
		this.baseView = baseView;
		this.indices = Arrays.stream(indices).boxed().collect(Collectors.toList());
	}
	
	@Override
	public int numAttributes() {
		return baseView.numAttributes();
	}
	
	@Override
	public int numInstances() {
		return indices.size();
	}
	
	@Override
	public EnumAttribute<?> attributeAt(int index) {
		return baseView.attributeAt(index);
	}
	
	@Override
	public Instance instanceAt(int index) {
		return baseView.instanceAt(indices.get(index));
	}

}
