package uni.ml.dataset;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * An instance represents a list of {@link Attribute}, {@link Value} pairs.
 * This is basically a row within a data table.
 * @author Julian Brummer
 *
 */
@Accessors(fluent=true)
public class Instance {	

	private Map<Attribute<?>, Value<?>> values = new HashMap<>();
	@Getter @Setter
	private float weight = 1.0f;
	
	/**
	 * Creates an instance with a initial list of entries.
	 */
	public Instance(Entry<?>... entries) {
		for (Entry<?> entry : entries) {
			add(entry);
		}
	}
	
	/**
	 * Returns whether an entry for the specified attribute is present within this instance.
	 */
	public boolean hasAttribute(Attribute<?> attribute) {
		return values.containsKey(attribute);
	}
	
	/**
	 * @return The value within the instance (row) at the specified attribute (column).
	 */
	public Value<?> value(Attribute<?> attribute) {
		return values.get(attribute);
	}
	
	/**
	 * Adds a new entry to the instance. 
	 * If an entry with the same attribute is already present within this instance, the value is replaced. 
	 */
	public <T extends Comparable<T>> void add(Attribute<T> attribute, Value<T> value) {
		values.put(attribute, value);
	}
	
	/**
	 * Adds a new entry to the instance. 
	 * If an entry with the same attribute is already present within this instance, the value is replaced. 
	 */
	public void addUnchecked(Attribute<?> attribute, Value<?> value) {
		values.put(attribute, value);
	}
	
	/**
	 * Adds a new entry to the instance. 
	 * If an entry with the same attribute is already present within this instance, the value is replaced. 
	 */
	public void add(Entry<?> entry) {
		values.put(entry.attribute, entry.value);
	}
	
	/**
	 * Multiplies the instance weight with the specified factor.
	 * @return The new weight.
	 */
	public float multiplyWeight(float factor) {
		return weight *= factor;
	}
	
	
	public String toString(Iterable<Attribute<?>> attributes) {
		StringBuilder b = new StringBuilder();
		//b.append("weight=").append(weight).append("  ");
		for (Attribute<?> attribute : attributes) {
			if (hasAttribute(attribute)) {
				b.append(value(attribute)).append(",");
			} else {
				b.append("---").append(",");
			}
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	@Override
	public String toString() {
		return toString(values.keySet());
	}

	/**
	 * A convenience class to represent an {@link Attribute}, {@link Value} pair.
	 * @author Julian Brummer
	 *
	 * @param <T> The type of the attribute/value.
	 */
	@AllArgsConstructor
	public static class Entry<T extends Comparable<T>> {
		public Attribute<T> attribute;
		public Value<T> value;
	}
	
	/**
	 * Convenience method to create an entry.
	 */
	public static <T extends Comparable<T>> Entry<T> entry(Attribute<T> attribute, Value<T> value) {
		return new Entry<T>(attribute, value);
	}
	
	
	/**
	 * Convenience method to create an entry.
	 */
	public static <T extends Comparable<T>> Entry<T> entry(Attribute<T> attribute, T v) {
		return new Entry<T>(attribute, new Value<T>(v));
	}
	
	/**
	 * Convenience method to create an instance.
	 */
	public static Instance instance(Entry<?>... entries) {
		return new Instance(entries);
	}
	
}
