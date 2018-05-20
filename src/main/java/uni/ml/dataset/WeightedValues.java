package uni.ml.dataset;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores a mapping of weighted values. 
 * @author Julian Brummer
 *
 */
public class WeightedValues {
	private Map<Value<?>, Value<?>> values = new HashMap<>();
	
	private void add(Value<?> value) {
		Value<?> clone = value.clone();
		clone.weight(0.0f);
		values.put(value, clone);
	}
	
	public WeightedValues(EnumAttribute<?> attribute) {
		for (Value<?> value : attribute) {
			add(value);
		}
	}
	
	public boolean contains(Value<?> value) {
		return values.containsKey(value);
	}
	
	public Value<?> get(Value<?> v) {
		return values.get(v);
	}
	
	public boolean updateWeight(Value<?> value, float weight) {
		if (!contains(value))
			return false;
		get(value).weight(weight);
		return true;
	}
	
	public boolean applyToWeight(Value<?> value, float weight) {
		if (!contains(value))
			return false;
		return updateWeight(value, get(value).weight()+weight);
	}
	
	public boolean isEmpty() {
		return values.isEmpty();
	}

	public Value<?> maxWeightedValue() {
		Value<?> maxWeightedValue = null;
		float w = Float.NEGATIVE_INFINITY;
		for (Value<?> value : values.keySet()) {
			Value<?> weightedValue = get(value);
			if (w < weightedValue.weight()) {
				w = weightedValue.weight();
				maxWeightedValue = weightedValue;
			}
		}
		return maxWeightedValue;
	}
}
