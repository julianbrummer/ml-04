package uni.ml.dataset;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;



/**
 * Stores information of an attribute within a dataset.
 * An attribute always has a name and a type <T>. 
 * Additionally, it may restrict the allowed values assignable to this attribute.
 * Note an attribute does not hold any values of a dataset, it is only used as a meta type.
 * 
 * @author Julian Brummer
 *
 * @param <T> The type of the attribute. This can be any type, but it must be comparable.
 */
@RequiredArgsConstructor
@Accessors(fluent=true)
@ToString(includeFieldNames=false, doNotUseGetters=true)
@EqualsAndHashCode()
public class Attribute<T extends Comparable<T>> {
	@NonNull @Getter
	private String name;
	
	/**
	 * Checks whether a value is allowed for this attribute.
	 * @param value The value to check.
	 */
	public boolean isAllowed(Value<?> value) {
		return true;
	}
}
