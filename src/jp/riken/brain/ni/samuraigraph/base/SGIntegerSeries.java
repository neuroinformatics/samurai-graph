package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * The series of integer numbers.
 *
 */
public class SGIntegerSeries implements Cloneable {

	/**
	 * The number for the start.
	 */
	private SGInteger mStart = null;
	
	/**
	 * The number for the end.
	 */
	private SGInteger mEnd = null;
	
	/**
	 * The step value.
	 */
	private SGInteger mStep = null;
	
	/**
	 * A constant for the end of the array.
	 */
	public static final String ARRAY_INDEX_END = "end";
	
	private static final String ERR_MSG_INVALID_STATE = "Invalid state.";

	/**
	 * The default constructor.
	 */
	public SGIntegerSeries() {
		super();
	}

	/**
	 * Checks whether given values are valid for an integer series.
	 * 
	 * @param start
	 *           the number for the start
	 * @param end
	 *           the number for the end
	 * @param step
	 *           the step value
	 * @param aliasMap
	 *           the map of alias
	 * @return true if given values are valid
	 */
	public static boolean isValidSeries(final SGInteger start, final SGInteger end, 
			final SGInteger step, Map<String, Integer> aliasMap) {
		if (start == null || end == null || step == null) {
			return false;
		}
		Integer nStart = start.getNumber();
		Integer nEnd = end.getNumber();
		Integer nStep = step.getNumber();
		Set<String> keySet = aliasMap.keySet();
		if (nStart == null) {
			if (!keySet.contains(start.getText())) {
				return false;
			}
		}
		if (nEnd == null) {
			if (!keySet.contains(end.getText())) {
				return false;
			}
		}
		if (nStep == null) {
			if (!keySet.contains(step.getText())) {
				return false;
			}
		}
		if (nStart != null && nEnd != null && nStep != null) {
			return isValidSeries(nStart, nEnd, nStep);
		}
		return true;
	}
	
	public static boolean isValidSeries(final SGInteger start, final SGInteger end, 
			final SGInteger step) {
		return isValidSeries(start, end, step, new HashMap<String, Integer>());
	}
	
	/**
	 * Checks whether given values are valid for an integer series.
	 * 
	 * @param start
	 *           the number for the start
	 * @param end
	 *           the number for the end
	 * @param step
	 *           the step value
	 * @return true if given values are valid
	 */
	public static boolean isValidSeries(final Integer start, final Integer end, final Integer step) {
		if (start == null || end == null || step == null) {
			return false;
		}
		final int startNum = start.intValue();
		final int endNum = end.intValue();
		final int stepNum = step.intValue();
		if (stepNum == 0 && (startNum != endNum)) {
			return false;
		}
		if (stepNum != 0) {
			if (startNum == endNum) {
				return true;
			}
			final boolean order = (startNum < endNum);
			final boolean stepSign = (stepNum > 0);
			if (order != stepSign) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds an object with given parameters.
	 * 
	 * @param start
	 *           the number for the start
	 * @param end
	 *           the number for the end
	 * @param step
	 *           the step value
	 */
	public SGIntegerSeries(final int start, final int end, final int step) {
		super();
		if (!isValidSeries(start, end, step)) {
			throw new IllegalArgumentException("Invalid input values: " + start + ":" + step + ":" + end);
		}
		this.mStart = new SGInteger(start);
		this.mEnd = new SGInteger(end, SGIntegerSeries.ARRAY_INDEX_END);
		this.mStep = new SGInteger(step);
	}

	/**
	 * Builds an object with given parameters.
	 * 
	 * @param start
	 *           the number for the start
	 * @param end
	 *           the number for the end
	 * @param step
	 *           the step value
	 */
	public SGIntegerSeries(final SGInteger start, final SGInteger end, final SGInteger step) {
		super();
		if (start == null || end == null || step == null) {
			throw new IllegalArgumentException("Invalid input values: " + start + ":" + step + ":" + end);
		}
		Integer nStart = start.getNumber();
		Integer nEnd = end.getNumber();
		Integer nStep = step.getNumber();
		if (nStart != null && nEnd != null && nStep != null) {
			if (!isValidSeries(nStart, nEnd, nStep)) {
				throw new IllegalArgumentException("Invalid input values: " + start + ":" + step + ":" + end);
			}
		}
		this.mStart = start;
		this.mEnd = end;
		this.mStep = step;
	}
	
	/**
	 * Builds an object with a given integer series.
	 * 
	 * @param series
	 *           an integer series
	 */
	public SGIntegerSeries(final SGIntegerSeries series) {
		super();
		if (series == null) {
			throw new IllegalArgumentException("series == null");
		}
		this.mStart = series.mStart;
		this.mEnd = series.mEnd;
		this.mStep = series.mStep;
	}
	
	/**
	 * Builds an object with a given integer value.
	 * 
	 * @param num
	 *           an integer value
	 */
	public SGIntegerSeries(final int num) {
		super();
		this.mStart = new SGInteger(num);
		this.mEnd = new SGInteger(num);
		this.mStep = new SGInteger(0);
	}
	
	/**
	 * Returns the number for the start.
	 * 
	 * @return the number for the start
	 */
	public SGInteger getStart() {
		return this.mStart;
	}
	
	/**
	 * Returns the number for the end.
	 * 
	 * @return the number for the end
	 */
	public SGInteger getEnd() {
		return this.mEnd;
	}

	/**
	 * Returns the step value.
	 * 
	 * @return the step value
	 */
	public SGInteger getStep() {
		return this.mStep;
	}

	/**
	 * Adds an alias for given integer value.
	 * 
	 * @param value
	 *           an integer value
	 * @param alias
	 *           the alias
	 */
	public void addAlias(final int value, final String alias) {
		if (alias == null) {
			throw new IllegalArgumentException("key == null");
		}
		
		// sets to the attributes
		this.setAlias(value, alias, this.mStart);
		this.setAlias(value, alias, this.mEnd);
		this.setAlias(value, alias, this.mStep);
	}
	
	private void setAlias(final int value, final String alias, SGInteger num) {
		num.addAlias(value, alias);
	}
	
	/**
	 * Returns the number of integer values.
	 * 
	 * @return the number of integer values
	 */
	public int getLength() {
		final Integer step = this.mStep.getNumber();
		if (step == null) {
			throw new Error(ERR_MSG_INVALID_STATE);
		}
		final Integer start = this.mStart.getNumber();
		if (start == null) {
			throw new Error(ERR_MSG_INVALID_STATE);
		}
		final Integer end = this.mEnd.getNumber();
		if (end == null) {
			throw new Error(ERR_MSG_INVALID_STATE);
		}
		if (step == 0) {
			return 1;
		}
		final int len = Math.abs(end - start) / Math.abs(step) + 1;
		return len;
	}

	/**
	 * Returns the list of numbers.
	 * 
	 * @return the list of numbers
	 */
	public List<SGInteger> getIntegerList() {
		List<SGInteger> list = new ArrayList<SGInteger>();
		final Integer start = this.mStart.getNumber();
		if (start == null) {
			return list;
		}
		final Integer end = this.mEnd.getNumber();
		if (end == null) {
			return list;
		}
		final Integer step = this.mStep.getNumber();
		if (step == null) {
			return list;
		}
		if (step > 0) {
			for (int num = start; num <= end; num += step) {
				list.add(new SGInteger(num));
			}
		} else if (step < 0) {
			for (int num = start; num >= end; num += step) {
				list.add(new SGInteger(num));
			}
		} else {
			list.add(this.mStart);
		}
		return list;
	}
	
	/**
	 * Returns the list of numbers.
	 * 
	 * @return the list of numbers
	 */
	public List<Integer> getNumberList() {
		List<SGInteger> integerList = this.getIntegerList();
		List<Integer> list = new ArrayList<Integer>();
		for (int ii = 0; ii < integerList.size(); ii++) {
			SGInteger num = integerList.get(ii);
			list.add(num.getNumber());
		}
		return list;
	}

	/**
	 * Returns sorted array of numbers.
	 * 
	 * @return sorted array of numbers
	 */
	public int[] getNumbers() {
		List<Integer> list = this.getNumberList();
		int[] array = new int[list.size()];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = list.get(ii);
		}
		
		// sorts the numbers
		Arrays.sort(array);
		
		return array;
	}

	/**
	 * Parses a given text string and returns an integer series object.
	 * 
	 * @param str
	 *          a text string
	 * @param aliasMap
	 *          the alias map
	 * @return an integer series object or null if failed to parse
	 */
	public static SGIntegerSeries parse(final String str, final Map<String, Integer> aliasMap) {
		if (str == null || aliasMap == null) {
			throw new IllegalArgumentException("str == null || aliasMap == null");
		}
		String[] array = str.split(":");
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return null;
		}
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = array[ii].trim();
		}
		SGInteger start = SGInteger.parse(array[0], aliasMap);
		if (start == null) {
			return null;
		}
		SGInteger end, step;
		if (array.length == 1) {
			end = start;
			step = new SGInteger(0);
		} else {
			SGInteger second = SGInteger.parse(array[1], aliasMap);
			if (second == null) {
				return null;
			}
			if (array.length == 2) {
				end = second;
				if (start.getNumber() != null && end.getNumber() != null) {
					final int sNum = start.getNumber().intValue();
					final int eNum = end.getNumber().intValue();
					final int num;
					if (sNum == eNum) {
						num = 0;
					} else {
						num = (sNum < eNum) ? 1 : -1;
					}
					step = new SGInteger(num);
				} else {
					if (SGUtility.equals(start, end)) {
						step = new SGInteger(0);
					} else if (SGUtility.equals(end.getText(), SGIntegerSeries.ARRAY_INDEX_END)) {
						step = new SGInteger(1);
					} else if (SGUtility.equals(start.getText(), SGIntegerSeries.ARRAY_INDEX_END)) {
						step = new SGInteger(-1);
					} else {
						return null;
					}
				}
			} else if (array.length == 3) {
				step = second;
				end = SGInteger.parse(array[2], aliasMap);
				if (end == null) {
					return null;
				}
			} else {
				return null;
			}
		}
		final Integer nStart = start.getNumber();
		final Integer nEnd = end.getNumber();
		final Integer nStep = step.getNumber();
		if (nStart != null && nEnd != null && nStep != null) {
			if (!SGIntegerSeries.isValidSeries(nStart, nEnd, nStep)) {
				return null;
			}
		}
		return new SGIntegerSeries(start, end, step);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.mStart.toString());
		final Integer step = this.mStep.getNumber();
		if (step != null && step != 0) {
			if (step != 1 && step != -1) {
				sb.append(':');
				sb.append(this.mStep.toString());
			}
			sb.append(':');
			sb.append(this.mEnd.toString());
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGIntegerSeries)) {
			return false;
		}
		SGIntegerSeries s = (SGIntegerSeries) obj;
		if (!this.mStart.equals(s.mStart)) {
			return false;
		}
		if (!this.mEnd.equals(s.mEnd)) {
			return false;
		}
		if (!this.mStep.equals(s.mStep)) {
			return false;
		}
		return true;
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
	
	/**
	 * Creates a list of integer series from given integer array.
	 * 
	 * @param numbers
	 *           an integer array
	 * @return a list of integer series
	 */
	public static List<SGIntegerSeries> createList(final int[] numbers) {
		if (numbers == null) {
			throw new IllegalArgumentException("numbers == null");
		}
		if (numbers.length == 0) {
			throw new IllegalArgumentException("numbers.length == 0");
		}
		if (numbers.length == 1) {
			List<SGIntegerSeries> seriesList = new ArrayList<SGIntegerSeries>();
			seriesList.add(new SGIntegerSeries(numbers[0]));
			return seriesList;
		}

		// sorts input arrays
		final int[] sortedNumbers = SGUtility.copyIntegerArray(numbers);
		Arrays.sort(sortedNumbers);
		
		// creates a map of lists
		int step = -1;
		int start = sortedNumbers[0];
		TreeMap<Integer, List<SGIntegerSeries>> rangeListMap = new TreeMap<Integer, List<SGIntegerSeries>>();
		int cnt = 1;
		for (int ii = 0; ii < sortedNumbers.length - 1; ii++) {
			final int d = sortedNumbers[ii + 1] - sortedNumbers[ii];
			if (d != step) {
				if (ii > 0) {
					updateRangeListMap(start, sortedNumbers[ii], step, rangeListMap, cnt);

					// clears local variables
					start = sortedNumbers[ii];
					cnt = 1;
				}
				step = d;
			} else {
				cnt++;
			}
			if (ii == sortedNumbers.length - 2) {
				updateRangeListMap(start, sortedNumbers[sortedNumbers.length - 1], step, rangeListMap, cnt);
			}
		}
		
		TreeMap<Integer, List<SGIntegerSeries>> dRangeListMap = new TreeMap<Integer, List<SGIntegerSeries>>(
				rangeListMap.descendingMap());
		List<SGIntegerSeries> rangeStore = new ArrayList<SGIntegerSeries>();
		List<SGIntegerSeries> overlapRangeStore = new ArrayList<SGIntegerSeries>();
		Iterator<Integer> itr = dRangeListMap.keySet().iterator();
		while (itr.hasNext()) {
			Integer key = itr.next();
			List<SGIntegerSeries> rangeList = dRangeListMap.get(key);
			for (SGIntegerSeries range : rangeList) {
				boolean overlapping = false;
				for (SGIntegerSeries r : rangeStore) {
					if (range.overlap(r)) {
						overlapping = true;
						break;
					}
				}
				if (!overlapping) {
					rangeStore.add(range);
				} else {
					overlapRangeStore.add(range);
				}
			}
		}
		
		Map<Integer, SGIntegerSeries> seriesMap = new TreeMap<Integer, SGIntegerSeries>();
		for (SGIntegerSeries range: rangeStore) {
			SGIntegerSeries series = new SGIntegerSeries(range.getStart().getNumber(), 
					range.getEnd().getNumber(), range.getStep().getNumber());
			seriesMap.put(range.getStart().getNumber(), series);
		}
		for (SGIntegerSeries range : overlapRangeStore) {
			List<SGIntegerSeries> seriesList = reduceOverlappingRange(range, rangeStore);
			for (SGIntegerSeries series : seriesList) {
				if (!seriesMap.values().contains(series)) {
					seriesMap.put(range.getStart().getNumber(), series);
				}
			}
		}
		
		List<SGIntegerSeries> seriesList = new ArrayList<SGIntegerSeries>();
		Set<Entry<Integer, SGIntegerSeries>> entrySet = seriesMap.entrySet();
		Iterator<Entry<Integer, SGIntegerSeries>> seriesItr = entrySet.iterator();
		while (seriesItr.hasNext()) {
			Entry<Integer, SGIntegerSeries> seriesEntry = seriesItr.next();
			SGIntegerSeries series = seriesEntry.getValue();
			seriesList.add(series);
		}
		return seriesList;
	}
	
	private static List<SGIntegerSeries> reduceOverlappingRange(SGIntegerSeries range, 
			List<SGIntegerSeries> rangeList) {
		List<Integer> reducedList = new ArrayList<Integer>();
		int[] numbers = range.getNumbers();
		for (int ii = 0; ii < numbers.length; ii++) {
			boolean contained = false;
			for (SGIntegerSeries series : rangeList) {
				List<Integer> numberList = series.getNumberList();
				if (numberList.contains(numbers[ii])) {
					contained = true;
					break;
				}
			}
			if (!contained) {
				reducedList.add(numbers[ii]);
			}
		}
		if (reducedList.size() == 0) {
			return new ArrayList<SGIntegerSeries>();
		}
		int[] reducedArray = new int[reducedList.size()];
		for (int ii = 0; ii < reducedArray.length; ii++) {
			reducedArray[ii] = reducedList.get(ii);
		}
		return SGIntegerSeries.createList(reducedArray);
	}

	private static void updateRangeListMap(final int start, final int end, final int step, 
			Map<Integer, List<SGIntegerSeries>> rangeListMap, int cnt) {
		SGIntegerSeries range = new SGIntegerSeries(start, end, step);
		List<SGIntegerSeries> rangeList = rangeListMap.get(cnt);
		if (rangeList == null) {
			rangeList = new ArrayList<SGIntegerSeries>();
			rangeListMap.put(cnt, rangeList);
		}
		rangeList.add(range);
	}
	
	/**
	 * Creates an instance with given array length.
	 * The start is set to zero, the end is set to the last index of array and the step is set to one.
	 * 
	 * @param len
	 *           array length
	 * @return created instance
	 */
	public static SGIntegerSeries createInstance(final int len) {
		if (len < 0) {
			throw new IllegalArgumentException("len < 0 : " + len);
		}
		if (len == 0) {
			return new SGIntegerSeries();
		} else {
			return new SGIntegerSeries(0, len - 1, 1);
		}
	}
	
	public boolean overlap(SGIntegerSeries series) {
		return SGUtilityNumber.isOverlapping(this.getStart().getNumber(), this.getEnd().getNumber(), 
				series.getStart().getNumber(), series.getEnd().getNumber());
	}
	
	public int getEndNumber() {
		return this.mEnd.getNumber();
	}
	
	public boolean isWithinRange(final int index) {
		final int min = this.getMin();
		final int max = this.getMax();
		return (index >= min && index <= max);
	}
	
	public int getMin() {
		List<Integer> numList = this.getNumberList();
		return numList.get(0);
	}
	
	public int getMax() {
		List<Integer> numList = this.getNumberList();
		return numList.get(numList.size() - 1);
	}
	
	public int search(final int index) {
		int[] indices = this.getNumbers();
		Arrays.sort(indices);
		return Arrays.binarySearch(indices, index);
	}
	
	public boolean contains(final int index) {
    	return (this.search(index) >= 0);
	}
	
	public List<Integer> getExisting(final int[] indices) {
		List<Integer> list = new ArrayList<Integer>();
		for (final int index : indices) {
			if (this.contains(index)) {
				list.add(index);
			}
		}
		return list;
	}

}
