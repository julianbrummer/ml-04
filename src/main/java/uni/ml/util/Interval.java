package uni.ml.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent=true)
@Getter
public class Interval {


	public enum Type {
		OPEN,
		CLOSED,
		L_OPEN,
		R_OPEN
	}
	
	private float left;
	private float right;
	private Type closureType;
	
	public boolean containsValue(float value) {
		switch (closureType) {
			case OPEN: return value > left && value < right;
			case CLOSED: return value >= left && value <= right;
			case L_OPEN: return value > left && value <= right;
			case R_OPEN: return value >= left && value < right;
			default: return false;
		}
	}
	
	@Override
	public String toString() {
		switch (closureType) {
			case OPEN: return "("+left+", "+right+")";
			case CLOSED: return "["+left+", "+right+"]";
			case L_OPEN: return "("+left+", "+right+"]";
			case R_OPEN: return "["+left+", "+right+")";
			default: return "";
		}
	}
	
}
