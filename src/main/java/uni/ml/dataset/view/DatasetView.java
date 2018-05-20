package uni.ml.dataset.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import uni.ml.dataset.Attribute;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Sampling;
import uni.ml.util.Interval;
import uni.ml.util.Interval.Type;


/**
 * A view on a subset of a dataset. 
 * A dataset can be decorated with multiple views.
 * For example a dataset can be decorated with an {@link DatasetIndexedView} to select instances, 
 * which itself can be decorated with a {@link DatasetPredicateView} to filter out instances according to some predicate.
 * 
 * @author Julian Brummer
 *
 */
@Accessors(fluent=true)
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class DatasetView {
	
	/**
	 * Enables iterating over all instances, attributes in a for loop. 
	 * @author Julian Brummer
	 */
	private interface ListIteratorBase<T> extends ListIterator<T>, Iterable<T> {

		int index = 0;

		@Override
		public default boolean hasPrevious() {
			return index-1 >= 0;
		}

		@Override
		public default int nextIndex() {
			return index;
		}

		@Override
		public default int previousIndex() {
			return index-1;
		}

		@Override
		public default void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void set(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void add(T e) {
			throw new UnsupportedOperationException();			
		}
		
		@Override
		public default Iterator<T> iterator() {
			return this;
		}
		
	}
	
	/**
	 * Enables iterating over all attributes in a for loop. 
	 * @author Julian Brummer
	 */
	private class AttributeIterator implements ListIteratorBase<EnumAttribute<?>> {

		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < numAttributes();
		}

		@Override
		public EnumAttribute<?> next() {
			if (index >= numAttributes())
				throw new NoSuchElementException();
			return attributeAt(index++);
		}

		@Override
		public EnumAttribute<?> previous() {
			return attributeAt(--index);
		}
		
	}
	
	/**
	 * Enables iterating over all instances in a for loop. 
	 * @author Julian Brummer
	 */
	private class InstanceIterator implements ListIteratorBase<Instance> {

		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < numInstances();
		}

		@Override
		public Instance next() {
			if (index >= numInstances())
				throw new NoSuchElementException();
			return instanceAt(index++);
		}

		@Override
		public Instance previous() {
			return instanceAt(--index);
		}
		
	}
	
	@Getter @Setter
	private String name = "unnamed";
	
// access methods for attributes and instances	
	
	public abstract int numAttributes();
	public abstract int numInstances();
	public abstract EnumAttribute<?> attributeAt(int index);
	public abstract Instance instanceAt(int index);

	public boolean hasAttributes() {
		return numAttributes() > 0;
	}
	
	public boolean hasInstances() {
		return numInstances() > 0;
	}
	
	public Iterable<EnumAttribute<?>> attributes() {
		return new AttributeIterator();
	}

	public Iterable<Instance> instances() {
		return new InstanceIterator();
	}
	
//
    
	
	/**
	 * A convenience method to access all attributes except for those that are explicitly excluded.
	 * @param exclude The attributes to exclude from the set.
	 * @return the set of attributes (columns) of the dataset without the excluded attributes.
	 */
	public Set<EnumAttribute<?>> attributeSet(EnumAttribute<?>... exclude) {
		Set<EnumAttribute<?>> attrSet = new HashSet<>();
		attributes().forEach(attrSet::add);
		attrSet.removeAll(Arrays.asList(exclude));
		return attrSet;
	}
	
	/**
	 * A convenience method to access the last attribute, e.g for classification.
	 * @return the last attribute or null if the dataset view has no attributes.
	 */
	public EnumAttribute<?> lastAttribute() {
		return hasAttributes()? attributeAt(numAttributes()-1) : null;
	}

	/**
	 * Splits the dataset(-view) randomly into a training- and a test set.
	 * @param ratio The ratio of the training set. Must be between 0 and 1.
	 * @return The training and test set.
	 */
	public DatasetSplit randomSplit(float ratio) {
		Sampling.Split split = Sampling.randomSplit(ratio, numInstances());
		return new DatasetSplit(new DatasetIndexedView(this, split.first()), new DatasetIndexedView(this, split.second()));
	}
	
	/**
	 * Normalizes the weights and samples instances from the weighted dataset(-view) with replacement.
	 */
	public DatasetView weightedBootstrapSampling() {
		normalizeWeights();
		List<Interval> distribution = new ArrayList<>();
		float margin = 0.0f;
		for (Instance instance : instances()) {
			distribution.add(new Interval(margin, margin+instance.weight(), Type.R_OPEN));
			margin += instance.weight();
		}
		return new DatasetIndexedView(this, Sampling.weightedBootstrap(distribution));
	}
	
	
	/**
	 * Assigns equal weights (1/numInstances()) to all instances of this dataset(-view).
	 */
	public void assignEqualWeights() {
		float w = 1.0f/numInstances();
		for (Instance instance : instances()) {
			instance.weight(w);
		}		
	}
	
	/**
	 * Normalizes the weights, such that the sum is one. 
	 */
	public void normalizeWeights() {
		float sumWeights = sumWeights();
		for (Instance instance : instances()) {
			instance.multiplyWeight(1/sumWeights);
		}	
	}
	
	/**
	 * @return The sum of all instance weights.
	 */
	public float sumWeights() {
		float sumWeights = 0.0f;
		for (Instance instance : instances()) {
			sumWeights += instance.weight();
		}	
		return sumWeights;
	}
	
	/**
	 * Saves the dataset(-view) to an ARFF file.
	 * @param dst The destination file.
	 * @throws IOException if file can not be created, UTF-8 encoding is not supported or an io exception occured.
	 */
	public void saveToArffFile(File file) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(file), "utf-8"))) {
			writer.write("@relation " + name);
			writer.write("\n");
			writer.write("\n");
			for (EnumAttribute<?> attribute : attributes()) {
				writer.write(attribute.arffString());
				writer.write("\n");
			}
			writer.write("\n");
			writer.write("@data\n");
			for (Instance instance : instances()) {
				writer.write(instance.arffString(attributes()));
				writer.write("\n");
			}
		} 
		
	}
	
	/**
	 * Saves the dataset(-view) to an ARFF file within the specified directory.
	 * @param dir The directory to save the file to. The file will have the same name as the dataset.
	 * @throws IOException if file can not be created, UTF-8 encoding is not supported or an io exception occured.
	 */
	public void saveToArff(File dir) throws IOException {
		saveToArffFile(new File(dir, name+".arff"));		
	}
	
	@Override
	public String toString() {
		// get list of attributes visible in view
		List<Attribute<?>> attributes = new ArrayList<>();
		for (int i = 0; i < numAttributes(); i++) {
			attributes.add(attributeAt(i));
		}
		
		// append attribute list
		StringBuilder b = new StringBuilder();
		if (hasAttributes()) {
			for (int i=0; i< numAttributes()-1; i++) {
				b.append(attributes.get(i)).append(",");
			}
			b.append(attributes.get(numAttributes()-1));
		}
		
		b.append("\n");
		
		// append instance list
		if (hasInstances()) {
			for (int i=0; i< numInstances()-1; i++) {
				b.append(instanceAt(i).toString(attributes)).append("\n");
			}
			b.append(instanceAt(numInstances()-1).toString(attributes));
		}
		return b.toString();
	}
	
	
	
}
