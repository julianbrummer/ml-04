package uni.ml.dataset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.ToString;
import uni.ml.util.Interval;

/**
 * Convenient class to sample instance indices.
 * @author Julian Brummer
 */
public class Sampling {
	
	@AllArgsConstructor
	@ToString(includeFieldNames=true)
	public static class Split {
		private List<Integer> first;
		private List<Integer> second;
		
		public int[] first() {
			return first.stream().mapToInt(i->i).toArray();
		}
		
		public int[] second() {
			return second.stream().mapToInt(i->i).toArray();
		}
		
		public int sizeFirst() {
			return first.size();
		}
		
		public int sizeSecond() {
			return second.size();
		}
	}
	
	/**
	 * Returns a ranged array of indices [from,to)
	 * @param from The start index (inclusive).
	 * @param to The end index (exclusive)
	 */
	public static int[] rangeArray(int from, int to) {
		return IntStream.range(from, to).toArray();
	}
	
	/**
	 * Returns a ranged list of indices [from,to)
	 * @param from The start index (inclusive).
	 * @param to The end index (exclusive)
	 */
	public static List<Integer> rangeList(int from, int to) {
		return Arrays.stream(rangeArray(from, to)).boxed().collect(Collectors.toList());
	}

	
	/**
	 * Returns a shuffled list of indices.
	 * @param numIndices The indices are numbered from 0 to numIndices-1
	 */
	public static List<Integer> shuffleList(int numIndices) {
		List<Integer> indexList = rangeList(0, numIndices);
		Collections.shuffle(indexList);
		return indexList;
	}
	
	/**
	 * Returns a shuffled array of indices.
	 * @param numIndices The indices are numbered from 0 to numIndices-1
	 */
	public static int[] shuffleArray(int numIndices) {
		return shuffleList(numIndices).stream().mapToInt(Integer::intValue).toArray();
	}
	
	/**
	 * Randomly splits a number of indices into a two sets (e.g. a for training/testing).
	 * @param ratio The training ratio.
	 * @param numIndices The indices are numbered from 0 to numIndices-1 
	 */
	public static Split randomSplit(float ratio, int numIndices) {
		int n = Math.min((int) Math.ceil(ratio*numIndices), numIndices);
		List<Integer> indexList = shuffleList(numIndices);
		return new Split(indexList.subList(0, n), indexList.subList(n, numIndices));
	}
	
	
	/**
	 * Randomly selects indices using weighted bootstraping.
	 * @param distribution The probability distribution for each index. 
	 */
	public static int[] weightedBootstrap(List<Interval> distribution) {
		int[] indices = new int[distribution.size()];
		for (int i = 0; i < indices.length; i++) {
			float draw = (float) Math.random();
			for (int j = 0; j < distribution.size(); j++) {
				if (distribution.get(j).containsValue(draw)) {
					indices[i] = j;
					break;
				}
			}	
		}
		return indices;
	}


	
}
