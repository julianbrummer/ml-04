package uni.ml.learning;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames=true)
public class ClassificationResult {
	public float mean;
	public float deviation;
}
