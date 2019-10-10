package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of the series of integer numbers.
 *
 */
public class SGIntegerSeriesSet implements Cloneable {

	/**
	 * The list of elements.
	 */
	private List<SGIntegerSeries> mSeriesList = new ArrayList<SGIntegerSeries>();
	
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
	public static boolean isValidInput(final int start, final int end, final int step) {
		return SGIntegerSeries.isValidSeries(start, end, step);
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
	public static boolean isValidInput(final SGInteger start, final SGInteger end, 
			final SGInteger step, Map<String, Integer> aliasMap) {
		return SGIntegerSeries.isValidSeries(start, end, step, aliasMap);
	}

	/**
	 * The default constructor.
	 */
	public SGIntegerSeriesSet() {
		super();
	}

	/**
	 * Builds an object with given series.
	 * 
	 * @param series
	 *           an integer series
	 */
	public SGIntegerSeriesSet(SGIntegerSeries series) {
		super();
		this.mSeriesList.add(series);
	}

	/**
	 * Builds an object with given series set.
	 * 
	 * @param seriesSet
	 *           a series set
	 */
	public SGIntegerSeriesSet(SGIntegerSeriesSet seriesSet) {
		super();
		this.mSeriesList = new ArrayList<SGIntegerSeries>(seriesSet.mSeriesList);
	}

	/**
	 * Builds an object with given start / end indices and the step.
	 * 
	 * @param start
	 *           the start index
	 * @param end
	 *           the end index
	 * @param step
	 *           the step
	 */
	public SGIntegerSeriesSet(final int start, final int end, final int step) {
		this(new SGIntegerSeries(start, end, step));
	}

	/**
	 * Builds an object with given start / end indices and the step.
	 * 
	 * @param start
	 *           the start index
	 * @param end
	 *           the end index
	 * @param step
	 *           the step
	 */
	public SGIntegerSeriesSet(final SGInteger start, final SGInteger end, final SGInteger step) {
		this(new SGIntegerSeries(start, end, step));
	}

	/**
	 * Builds an object with given index.
	 * 
	 * @param index
	 *           the index
	 */
	public SGIntegerSeriesSet(final int index) {
		this(new SGIntegerSeries(index));
	}

	/**
	 * Creates an instance with given array length.
	 * The start is set to zero, the end is set to the last index of array and the step is set to one.
	 * 
	 * @param len
	 *           array length
	 * @return created instance
	 */
	public static SGIntegerSeriesSet createInstance(final int len) {
		SGIntegerSeries series = SGIntegerSeries.createInstance(len);
		return new SGIntegerSeriesSet(series);
	}

	/**
	 * Builds an object with a given integer array.
	 * 
	 * @param numbers
	 *           an integer array
	 */
	public SGIntegerSeriesSet(final int[] numbers) {
		this();
		if (numbers == null) {
			throw new IllegalArgumentException("numbers == null");
		}
		if (numbers.length == 0) {
			throw new IllegalArgumentException("numbers.length == 0");
		}
		if (numbers.length == 1) {
			this.mSeriesList = new ArrayList<SGIntegerSeries>();
			this.mSeriesList.add(new SGIntegerSeries(numbers[0]));
		} else {
			this.mSeriesList.addAll(SGIntegerSeries.createList(numbers));
		}
	}
	
	/**
	 * Clears all numbers.
	 */
	public void clear() {
		this.mSeriesList.clear();
	}
	
	/**
	 * Adds an integer number.
	 * 
	 * @param num
	 *          an integer number
	 */
	public void add(final int num) {
		this.mSeriesList.add(new SGIntegerSeries(num));
	}
	
	/**
	 * Adds an integer number series.
	 * 
	 * @param series
	 *           an integer number series
	 */
	public void add(SGIntegerSeries series) {
		this.mSeriesList.add(new SGIntegerSeries(series));
	}
	
	/**
	 * Returns a list of integer series.
	 * 
	 * @return a list of integer series
	 */
	public List<SGIntegerSeries> getSeriesList() {
		return new ArrayList<SGIntegerSeries>(this.mSeriesList);
	}
	
	/**
	 * Tries to get an integer series.
	 * If all values are equally-spaces numbers, returns a series object, otherwise returns null.
	 * 
	 * @return a series object or null
	 */
	public SGIntegerSeries testReduce() {
		if (this.mSeriesList.size() == 1) {
			return this.mSeriesList.get(0);
		}
		final int[] array = this.getNumbers();
		if (array.length == 1) {
			return new SGIntegerSeries(array[0]);
		}
		int step = 0;
		for (int ii = 0; ii < array.length - 1; ii++) {
			final int cur = array[ii];
			final int next = array[ii + 1];
			final int diff = next - cur;
			if (step == 0) {
				step = diff;
			} else {
				if (step != diff) {
					return null;
				}
			}
		}
		return new SGIntegerSeries(array[0], array[array.length - 1], step);
	}
	
	/**
	 * Returns sorted array of numbers.
	 * 
	 * @return sorted array of numbers
	 */
	public int[] getNumbers() {
		List<Integer> numList = new ArrayList<Integer>();
		for (int ii = 0; ii < this.mSeriesList.size(); ii++) {
			SGIntegerSeries el = this.mSeriesList.get(ii);
			List<Integer> list = el.getNumberList();
			numList.addAll(list);
		}
		
		// remove overlapping values
		List<Integer> numListNew = new ArrayList<Integer>();
		Set<Integer> numSet = new HashSet<Integer>();
		for (int ii = 0; ii < numList.size(); ii++) {
			Integer num = numList.get(ii);
			if (!numSet.contains(num)) {
				numListNew.add(num);
			}
			numSet.add(num);
		}
		
		int[] array = new int[numListNew.size()];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = numListNew.get(ii);
		}
		
		// sorts the numbers
		Arrays.sort(array);
		
		return array;
	}

	/**
	 * Returns the length of integer arrays.
	 * 
	 * @return the length of integer arrays
	 */
	public int getLength() {
		return this.getNumbers().length;
	}
	
	/**
	 * Returns the end index.
	 * 
	 * @return the end index
	 */
	public Integer getEndIndex() {
		if (this.mSeriesList.size() > 0) {
			SGIntegerSeries firstSeries = this.mSeriesList.get(0);
			SGInteger end = firstSeries.getEnd();
			return end.getAliasMap().get(SGIntegerSeries.ARRAY_INDEX_END);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int ii = 0; ii < this.mSeriesList.size(); ii++) {
			if (ii > 0) {
				sb.append(',');
			}
			SGIntegerSeries el = this.mSeriesList.get(ii);
			sb.append(el.toString());
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGIntegerSeriesSet)) {
			return false;
		}
		SGIntegerSeriesSet s = (SGIntegerSeriesSet) obj;
		return this.mSeriesList.equals(s.mSeriesList);
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
            SGIntegerSeriesSet set = (SGIntegerSeriesSet) super.clone();
            set.mSeriesList = new ArrayList<SGIntegerSeries>(this.mSeriesList);
            return set;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
	
	/**
	 * Parses a given text string and returns an integer series set.
	 * 
	 * @param str
	 *          a text string
	 * @param aliasMap
	 *          the alias map
	 * @return an integer series set or null if failed to parse
	 */
	public static SGIntegerSeriesSet parse(final String str, final Map<String, Integer> aliasMap) {
		if (str == null || aliasMap == null) {
			throw new IllegalArgumentException("str == null || aliasMap == null");
		}
		String[] array = str.split(",");
		if (array == null) {
			return null;
		}
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = array[ii].trim();
		}
		SGIntegerSeriesSet set = new SGIntegerSeriesSet();
		for (int ii = 0; ii < array.length; ii++) {
			Integer num = SGUtilityText.getInteger(array[ii]);
			if (num != null) {
				set.add(num);
			} else {
				SGIntegerSeries series = SGIntegerSeries.parse(array[ii], aliasMap);
				if (series != null) {
					set.add(series);
				} else {
					return null;
				}
			}
		}
		return set;
	}

	/**
	 * Parses a given text string and array length and returns an integer series set.
	 * 
	 * @param str
	 *           a text string
	 * @param len
	 *           array length
	 * @return an integer series set or null if failed to parse
	 */
    public static SGIntegerSeriesSet parse(String str, final int len) {
        Map<String, Integer> aliases = new HashMap<String, Integer>();
        aliases.put(SGIntegerSeries.ARRAY_INDEX_END, len - 1);
        SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(str, aliases);
        if (indices == null) {
        	return null;
        }
        int[] numArray = indices.getNumbers();
        for (int ii = 0; ii < numArray.length; ii++) {
            if (numArray[ii] < 0 || numArray[ii] >= len) {
            	return null;
            }
        }
        return indices;
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
		for (SGIntegerSeries series : this.mSeriesList) {
			series.addAlias(value, alias);
		}
	}
	
	/**
	 * Returns true if this object has all index from 0 to the end.
	 * 
	 * @return true if this object has all index from 0 to the end
	 */
    public boolean isComplete() {
//    	SGIntegerSeries series = this.testReduce();
//    	if (series == null) {
//    		return false;
//    	}
//    	String str = series.toString();
//    	return "0:end".equals(str);
    	Integer end = this.getEndIndex();
    	if (end == null) {
    		return false;
    	}
    	return (end == this.getLength() - 1);
    }
    
	public boolean contains(final int index) {
		for (SGIntegerSeries series : this.mSeriesList) {
			if (series.contains(index)) {
				return true;
			}
		}
		return false;
	}
	
	public List<Integer> getExisting(final int[] indices) {
		Set<Integer> set = new TreeSet<Integer>();
		for (SGIntegerSeries series : this.mSeriesList) {
			set.addAll(series.getExisting(indices));
		}
		return new ArrayList<Integer>(set);
	}
	
	public int search(final int index) {
		int[] indices = this.getNumbers();
		Arrays.sort(indices);
		return Arrays.binarySearch(indices, index);
	}
}
