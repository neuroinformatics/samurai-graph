package jp.riken.brain.ni.samuraigraph.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class with utility methods for the calculation of numbers.
 * 
 */
public class SGUtilityNumber implements SGIConstants {

    /**
     * Returns the order of number. For example: 35.5 -> 1, -121.1 -> 2, 0.025 ->
     * -2.
     * 
     * @param d
     *            number to get order
     * @return the order of the number
     * @throws IllegalArgumentException
     *             input value is equal to 0.0
     */
    public static int getOrder(final double d) {
        if (d == 0.0) {
            throw new IllegalArgumentException("d == 0.0");
        }
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException("d is infinite value");
        }
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("d is not a number");
        }

        final double absd = Math.abs(d);
        final double log = Math.log10(absd);
        int order = (int) Math.floor(log);
        return order;
    }

    /**
     * Returns ten to the power of given ordinal number.
     * 
     * @param order -
     *            ordinal number
     * @return ten to the power of given ordinal
     */
    public static double getPowersOfTen(final int order) {
        BigDecimal bd = getBigDecimalPowersOfTen(order);
        return bd.doubleValue();
    }

    /**
     * Returns ten to the power of given ordinal number in BigDecimal form.
     * 
     * @param order -
     *            ordinal number
     * @return ten to the power of given ordinal
     */
    public static BigDecimal getBigDecimalPowersOfTen(final int order) {
        BigDecimal bd = new BigDecimal(1.0);
        bd = bd.movePointRight(order);
        return bd;
    }

    /**
     * Truncate the number at given digit. For example: value=8715.61, digit=2 ->
     * 8700.0
     * 
     * @param value -
     *            number to be truncated
     * @param digit -
     *            digit
     * @return truncated number
     */
    public static double truncateNumber(final double value, final int digit) {
        return truncateNumber(Double.toString(value), digit);
    }

    /**
     * Truncate the number at given digit.
     * 
     * @param value -
     *            a text string of the number to be truncated
     * @param digit -
     *            digit
     * @return truncated number
     */
    public static double truncateNumber(final String value, final int digit) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointLeft(digit);
        final long num = (long) bd.doubleValue();
        bd = new BigDecimal(num);
        bd = bd.movePointRight(digit);
        return bd.doubleValue();
    }

    /**
     * Round off the number at given digit. For example: value=8715.61, digit=1 ->
     * 8700.0 and value=8765.61, digit=1 -> 8800.0
     * 
     * @param value -
     *            number to be rounded off
     * @param digit -
     *            digit
     * @return the result of rounded off
     */
    public static double roundOffNumber(final double value, final int digit) {
        return roundOffNumber(Double.toString(value), digit);
    }

    /**
     * Round off the number at given digit. For example: value=8715.61, digit=1 ->
     * 8700.0 and value=8765.61, digit=1 -> 8800.0
     * 
     * @param value -
     *            a text string of the number to be rounded off
     * @param digit -
     *            digit
     * @return the result of rounded off
     */
    public static double roundOffNumber(final String value, final int digit) {
        BigDecimal bd = new BigDecimal(value);
	double sign = bd.doubleValue() >= 0.0 ? 1.0 : -1.0;
        bd = bd.movePointLeft(digit + 1);
//        final double num = Math.rint(bd.doubleValue());
        final double num = sign * (long) (Math.abs(bd.doubleValue()) + 0.50);
        bd = new BigDecimal(num);
        bd = bd.movePointRight(digit + 1);
        return bd.doubleValue();
    }

    /**
     * Round out the number at given digit. For example: value=8715.61, digit=1 ->
     * 8800.0
     * 
     * @param value -
     *            number to be rounded out
     * @param digit -
     *            digit
     * @return the result of rounded off
     */
    public static double roundOutNumber(final double value, final int digit) {
        return roundOutNumber(Double.toString(value), digit);
    }

    /**
     * Round out the number at given digit. For example: value=8715.61, digit=1 ->
     * 8800.0
     * 
     * @param value -
     *            a text string of the number to be rounded off
     * @param digit -
     *            digit
     * @return the result of rounded off
     */
    public static double roundOutNumber(final String value, final int digit) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointLeft(digit + 1);
        final double num = Math.ceil(bd.doubleValue());
        bd = new BigDecimal(num);
        bd = bd.movePointRight(digit + 1);
        return bd.doubleValue();
    }

    /**
     * Returns whether given two ranges are overlapping.
     * 
     * @param start1
     *           the start value of the first range
     * @param end1
     *           the end value of the first range
     * @param start2
     *           the start value of the second range
     * @param end2
     *           the end value of the second range
     * @return true if given two ranges are overlapping
     */
    public static boolean isOverlapping(final double start1, final double end1,
            final double start2, final double end2) {
        final double value = getOverlap(start1, end1, start2, end2);
        return (value != 0.0);
    }

    /**
     * Returns the size of overlapping between two ranges.
     * 
     * @param start1
     *           the start value of the first range
     * @param end1
     *           the end value of the first range
     * @param start2
     *           the start value of the second range
     * @param end2
     *           the end value of the second range
     * @return the size of overlapping between two ranges
     */
    public static double getOverlap(final double start1, final double end1,
            final double start2, final double end2) {
        final double min1 = start1 < end1 ? start1 : end1;
        final double max1 = start1 < end1 ? end1 : start1;
        final double min2 = start2 < end2 ? start2 : end2;
        final double max2 = start2 < end2 ? end2 : start2;
        if (max1 < min2 || max2 < min1) {
        	return 0.0;
        }
        double value;
        if (min1 < min2) {
            value = max1 - min2;
        } else {
            value = max2 - min1;
        }
        return value;
    }

    /**
     * Returns whether given two ranges are overlapping.
     * 
     * @param start1
     *           the start value of the first range
     * @param end1
     *           the end value of the first range
     * @param start2
     *           the start value of the second range
     * @param end2
     *           the end value of the second range
     * @return true if given two ranges are overlapping
     */
    public static boolean isOverlapping(final int start1, final int end1,
            final int start2, final int end2) {
        final int min1 = start1 < end1 ? start1 : end1;
        final int max1 = start1 < end1 ? end1 : start1;
        final int min2 = start2 < end2 ? start2 : end2;
        final int max2 = start2 < end2 ? end2 : start2;
        return  !(max1 < min2 || max2 < min1);
    }

    /**
     * 
     * @param min
     * @param max
     * @param value
     * @return
     */
    public static boolean contains(final double x1, final double x2,
            final double value) {
        double min;
        double max;
        if (x1 <= x2) {
            min = x1;
            max = x2;
        } else {
            min = x2;
            max = x1;
        }
        return (min <= value && value <= max);
    }

    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static boolean contains(final double x1, final double y1,
            final double x2, final double y2) {
        final double min1 = x1 < y1 ? x1 : y1;
        final double max1 = x1 < y1 ? y1 : x1;
        final double min2 = x2 < y2 ? x2 : y2;
        final double max2 = x2 < y2 ? y2 : x2;
        return (min1 <= min2 && max2 <= max1);
    }

    /**
     * 
     * @param flag
     * @param value
     * @param min
     * @param max
     * @param step
     * @param err
     * @return
     */
    public static double stepValue(final boolean flag, final double value,
            final double min, final double max, final double step,
            final double err) {

        // final int indexMax = (int)Math.rint(max/step);

        final int indexNearest = (int) Math.rint(value / step);
        final double valueNearest = indexNearest * step;

        int indexNew;
        if (Math.abs(value - valueNearest) < err) {
            if (flag) {
                indexNew = indexNearest + 1;
            } else {
                indexNew = indexNearest - 1;
            }
        } else {
            if (flag) {
                indexNew = (int) Math.floor(value / step) + 1;
            } else {
                indexNew = (int) Math.floor(value / step);
            }
        }

        final double valueNew = indexNew * step;

        return valueNew;
    }

    /**
     * Returns a rounded number that is the order of given number to given 
     * significant digits with given mode of rounding.
     * 
     * @param value
     *           the number to be rounded
     * @param num
     *           a number to specify the order
     * @param digit
     *           significant digits
     * @param mode
     *           the mode of rounding
     * @return a rounded number
     */
    public static double getNumberInNumberOrder(final double value,
            final double num, final int digit, final int mode) {
    	if (value == 0.0) {
    		return value;
    	}
        final int order = SGUtilityNumber.getOrder(num) - digit + 1;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointLeft(order);
        bd = bd.setScale(0, mode);
        bd = bd.movePointRight(order);
        return bd.doubleValue();
    }

    /**
     * Returns a rounded number that is the order of given value range to given 
     * significant digits with given mode of rounding.
     * 
     * @param value
     *           the number to be rounded
     * @param min
     *           the minimum value
     * @param max
     *           the maximum value
     * @param digit
     *           significant digits
     * @param mode
     *           the mode of rounding
     * @return a rounded number
     */
    public static double getNumberInRangeOrder(final double value,
            final double min, final double max, final int digit, final int mode) {
    	if (max == min) {
            return getNumberInNumberOrder(value, max, digit, mode);
    	}
        return getNumberInNumberOrder(value, max - min, digit, mode);
    }

    /**
     * Return the minimum value in an array of double numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the minimum value
     */
    public static double min(final double[] array) {
    	boolean valid = false;
        double min = Double.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
        	if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
        		continue;
        	}
            if (array[ii] < min) {
                min = array[ii];
                valid = true;
            }
        }
        if (!valid) {
        	return Double.NaN;
        }
        return min;
    }
    
    /**
     * Return the minimum value in an array of float numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the minimum value
     */
    public static float min(final float[] array) {
    	boolean valid = false;
    	float min = Float.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
        	if (Float.isNaN(array[ii]) || Float.isInfinite(array[ii])) {
        		continue;
        	}
            if (array[ii] < min) {
                min = array[ii];
                valid = true;
            }
        }
        if (!valid) {
        	return Float.NaN;
        }
        return min;
    }

    /**
     * Return the minimum value in an array of integer numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the minimum value
     */
    public static int min(final int[] array) {
    	int min = Integer.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] < min) {
                min = array[ii];
            }
        }
        return min;
    }

    /**
     * Return the minimum values in an array of float number tuples.
     * 
     * @param array
     *            an array to be searched
     * @return the minimum value
     */
    public static SGTuple2f min(final SGTuple2f[] array) {
    	float[] xArray = new float[array.length];
    	for (int ii = 0; ii < array.length; ii++) {
    		xArray[ii] = array[ii].x;
    	}
    	float[] yArray = new float[array.length];
    	for (int ii = 0; ii < array.length; ii++) {
    		yArray[ii] = array[ii].y;
    	}
    	final float xMin = min(xArray);
    	final float yMin = min(yArray);
        return new SGTuple2f(xMin, yMin);
    }

    /**
     * Return the maximum value in an array of double numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the maximum value
     */
    public static double max(final double[] array) {
    	boolean valid = false;
        double max = - Double.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
        	if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
        		continue;
        	}
            if (array[ii] > max) {
                max = array[ii];
                valid = true;
            }
        }
        if (!valid) {
        	return Double.NaN;
        }
        return max;
    }

    public static double absMax(final double[] array) {
    	boolean valid = false;
        double max = - Double.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
        	if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
        		continue;
        	}
        	final double absValue = Math.abs(array[ii]);
            if (absValue > max) {
                max = absValue;
                valid = true;
            }
        }
        if (!valid) {
        	return Double.NaN;
        }
        return max;
    }

    /**
     * Return the maximum value in an array of float numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the maximum value
     */
    public static float max(final float[] array) {
    	boolean valid = false;
    	float max = -Float.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
        	if (Float.isNaN(array[ii]) || Float.isInfinite(array[ii])) {
        		continue;
        	}
            if (array[ii] > max) {
                max = array[ii];
                valid = true;
            }
        }
        if (!valid) {
        	return Float.NaN;
        }
        return max;
    }

    /**
     * Return the maximum value in an array of integer numbers.
     * 
     * @param array
     *            an array to be searched
     * @return the maximum value
     */
    public static int max(final int[] array) {
    	int max = - Integer.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] > max) {
                max = array[ii];
            }
        }
        return max;
    }

    /**
     * Return the maximum values in an array of float number tuples.
     * 
     * @param array
     *            an array to be searched
     * @return the maximum value
     */
    public static SGTuple2f max(final SGTuple2f[] array) {
    	float[] xArray = new float[array.length];
    	for (int ii = 0; ii < array.length; ii++) {
    		xArray[ii] = array[ii].x;
    	}
    	float[] yArray = new float[array.length];
    	for (int ii = 0; ii < array.length; ii++) {
    		yArray[ii] = array[ii].y;
    	}
    	final float xMax = max(xArray);
    	final float yMax = max(yArray);
        return new SGTuple2f(xMax, yMax);
    }

    public static Set<SGAxisValue> calcStepValue(
    		final SGAxisValue min, final SGAxisValue max,
    		final SGAxisValue baseline, SGAxisStepValue step, 
    		final int digit) {
        Set<SGAxisValue> set = new HashSet<SGAxisValue>();
        
        if (baseline.compareTo(min) < 0) {
            // when the baseline is smaller than the minimum value
        	SGAxisValue value;
        	
        	if (step instanceof SGAxisDoubleStepValue) {
            	// sets up the start value
        		final double dMin = min.getValue();
        		final double dBaseline = baseline.getValue();
        		final double dStep = Math.abs(((SGAxisDoubleStepValue) step).getValue());
            	final double offset = (dMin - dBaseline) / dStep;
            	final long nOffset = (long) offset;
                double dValue = dBaseline + nOffset * dStep;
                value = new SGAxisDoubleValue(dValue);
        	} else {
            	value = baseline;
        	}
            value = value.adjustValue(min, max, digit);
            
            boolean first = true;
            while (true) {
            	SGAxisValue prev = value;
            	SGAxisValue valueNew = value.plus(step);
            	valueNew = valueNew.adjustValue(min, max, digit);
                if (valueNew.equals(value)) {
                	break;
                }
                if(valueNew.compareTo(value) < 0) {
                	step = step.negated();
                	continue;
                }
                value = valueNew;
                if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
                	if (first) {
                        // add one number smaller than the minimum value
                        set.add(prev);
                        first = false;
                	}
                    set.add(value);
                } else if (value.compareTo(max) > 0) {
                    break;
                }
            }
            
            // add one number larger then the maximum number
            set.add(value);
            
        } else if (baseline.compareTo(max) > 0) {
            // when the baseline is larger than the maximum value
        	SGAxisValue value;
        	
        	if (step instanceof SGAxisDoubleStepValue) {
            	// sets up the start value
        		final double dMax = max.getValue();
        		final double dBaseline = baseline.getValue();
        		final double dStep = Math.abs(((SGAxisDoubleStepValue) step).getValue());
            	final double offset = (dBaseline - dMax) / dStep;
            	final long nOffset = (long) offset;
                double dValue = dBaseline - nOffset * dStep;
                value = new SGAxisDoubleValue(dValue);
        	} else {
            	value = baseline;
        	}
            value = value.adjustValue(min, max, digit);

            boolean first = true;
            while (true) {
            	SGAxisValue prev = value;
            	SGAxisValue valueNew = value.minus(step);
            	valueNew = valueNew.adjustValue(min, max, digit);
                if (valueNew.equals(value)) {
                	break;
                }
                if(valueNew.compareTo(value) > 0) {
                	step = step.negated();
                	continue;
                }
                value = valueNew;
                if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
                	if (first) {
                        // add one number smaller than the minimum value
                        set.add(prev);
                        first = false;
                	}
                    set.add(value);
                } else if (value.compareTo(min) < 0) {
                    break;
                }
            }

            // add one number smaller than the minimum value
            set.add(value);
           
        } else {
            // when the baseline is within the range
        	SGAxisValue value = baseline;
            set.add(value);
            
            while (true) {
            	SGAxisValue valueNew = value.plus(step);
            	valueNew = valueNew.adjustValue(min, max, digit);
                if (valueNew.equals(value)) {
                	break;
                }
                if(valueNew.compareTo(value) < 0) {
                	step = step.negated();
                	continue;
                }
                value = valueNew;
                if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
                    set.add(value);
                } else if (value.compareTo(max) > 0) {
                    break;
                }
            }
            
            // add one number larger then the maximum number
            set.add(value);
            
            value = baseline;
            while (true) {
            	SGAxisValue valueNew = value.minus(step);
            	valueNew = valueNew.adjustValue(min, max, digit);
                if (valueNew.equals(value)) {
                	break;
                }
                if(valueNew.compareTo(value) > 0) {
                	step = step.negated();
                	continue;
                }
                value = valueNew;
                if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
                    set.add(value);
                } else if (value.compareTo(min) < 0) {
                    break;
                }
            }
            
            // add one number smaller than the minimum value
            set.add(value);
        }
        
        return set;
    }

    /**
     * Calculates sorted step values between given range, baseline and step.
     * 
     * @param min
     *           the minimum value
     * @param max
     *           the maximum value
     * @param baseline
     *           the baseline value
     * @param step
     *           the step value
     * @param digit
     *           significant digits
     * @return an array of sorted step values
     */
    public static double[] calcStepValueSorted(final double min, final double max,
    		final double baseline, final double step, final int digit) {
        SGAxisValue[] values = SGUtilityNumber.calcStepValueSorted(
        		new SGAxisDoubleValue(min), 
        		new SGAxisDoubleValue(max), 
        		new SGAxisDoubleValue(baseline), 
        		new SGAxisDoubleStepValue(step), 
        		digit);
        double[] ret = new double[values.length];
        for (int ii = 0; ii < values.length; ii++) {
        	ret[ii] = values[ii].getValue();
        }
        return ret;
    }

    public static SGAxisValue[] calcStepValueSorted(
    		final SGAxisValue min, final SGAxisValue max,
    		final SGAxisValue baseline, final SGAxisStepValue step, final int digit) {
    	Set<SGAxisValue> set = calcStepValue(min, max, baseline, step, digit);
    	List<SGAxisValue> list = new ArrayList<SGAxisValue>(set);
    	SGAxisValue[] array = new SGAxisValue[list.size()];
    	for (int ii = 0; ii < array.length; ii++) {
    		array[ii] = list.get(ii);
    	}
    	Arrays.sort(array);
    	return array;
    }

    /**
     * Transforms a list of double numbers to an array.
     * 
     * @param list
     *           a list of double numbers
     * @return an array of double numbers
     */
    public static double[] toArray(List<Double> list) {
    	double[] array = new double[list.size()];
    	for (int ii = 0; ii < array.length; ii++) {
    		Double d = list.get(ii);
    		array[ii] = d.doubleValue();
    	}
    	return array;
    }

    public static double getNumberInRangeOrder(final double value, final SGAxis axis,
    		final int digit, final int mode) {
        final int type = axis.getScaleType();
        if (type == SGAxis.LINEAR_SCALE) {
            return SGUtilityNumber.getNumberInRangeOrder(value, axis
                    .getMinDoubleValue(), axis.getMaxDoubleValue(),
                    digit, mode);
        } else if (type == SGAxis.LOG_SCALE) {
            if (value == 0.0) {
            	return 0.0;
            } else {
                final int order = SGUtilityNumber.getOrder(value);
                final double min = SGUtilityNumber.getPowersOfTen(order);
                final double max = min * 10.0;
                return SGUtilityNumber.getNumberInRangeOrder(value, min, max,
                        digit, mode);
            }
        } else {
            throw new IllegalArgumentException("scale type is invalid");
        }
    }
    
    public static double getNumberInRangeOrder(final double value, final SGAxis axis,
            final int mode) {
    	return SGUtilityNumber.getNumberInRangeOrder(value, axis, SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT, mode);
    }

    public static double getNumberInRangeOrder(final double value, final SGAxis axis) {
        return getNumberInRangeOrder(value, axis, BigDecimal.ROUND_HALF_UP);
    }

    public static double getNumberInRangeOrder(final double value, final double[] values,
    		final int digit) {
    	final double min = SGUtilityNumber.min(values);
    	final double max = SGUtilityNumber.max(values);
        return SGUtilityNumber.getNumberInRangeOrder(value, min, max,
        		digit, BigDecimal.ROUND_HALF_UP);
    }

    public static double getNumberInNumberOrder(final double value, final double ref) {
        return getNumberInNumberOrder(value, ref, SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Returns an array of strings if they exist.
     * 
     * @param dp
     *          decimal places
     * @param exp
     *          exponent
     * @return string array
     */
    public static String[] getStringArray(final double[] values, final int dp, final int exp) {

        if (dp < 0) {
            throw new IllegalArgumentException("Decimal places must not be negatie: " + dp);
        }
        
        // create NumberFormat object
        NumberFormat nf = SGUtilityNumber.createNumberFormat(dp);

        // create an array of text strings for tick labels
        final int len = values.length;
        String[] sArray = new String[len];
        for (int ii = 0; ii < len; ii++) {
            sArray[ii] = SGUtilityNumber.createExponentialString(values[ii], nf, exp);
        }
        
        return sArray;
    }

    /**
     * Create a pattern and a decimal format.
     * 
     * @param dp
     *          the decimal places
     */
    private static NumberFormat createNumberFormat(final int dp) {
        // create a pattern and a decimal format
        StringBuffer sbPattern = new StringBuffer();
        sbPattern.append('0');
        if (dp > 0) {
            sbPattern.append('.');
            for (int ii = 0; ii < dp; ii++) {
                sbPattern.append('0');
            }
        }
        String pattern = sbPattern.toString();
        NumberFormat nf = new DecimalFormat(pattern);
        return nf;
    }
    
    /**
     * Create a text string with given exponent.
     * 
     * @param value
     *           a value
     * @param nf
     *           the number format
     * @param exp
     *           the exponent
     * @return created text string
     */
    private static String createExponentialString(final double value, 
            final NumberFormat nf, final int exp) {
        if (Double.isNaN(value)) {
            return "";
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.movePointLeft(exp);
        StringBuffer sb = new StringBuffer();
        String str = nf.format(bd.doubleValue());
        sb.append(str);
        if (exp != 0) {
            sb.append('E');
            sb.append(exp);
        }
        return sb.toString();
    }

    /**
     * Returns the smaller value.
     * 
     * @param v1
     *           the first value
     * @param v2
     *           the second value
     * @return the smaller value
     */
    public static double min(final double v1, final double v2) {
    	return min(new double[] { v1, v2 });
    }

    /**
     * Returns the larger value.
     * 
     * @param v1
     *           the first value
     * @param v2
     *           the second value
     * @return the larger value
     */
    public static double max(final double v1, final double v2) {
    	return max(new double[] { v1, v2 });
    }

    /**
     * Returns the smaller value.
     * 
     * @param v1
     *           the first value
     * @param v2
     *           the second value
     * @return the smaller value
     */
    public static float min(final float v1, final float v2) {
    	return min(new float[] { v1, v2 });
    }

    /**
     * Returns the larger value.
     * 
     * @param v1
     *           the first value
     * @param v2
     *           the second value
     * @return the larger value
     */
    public static float max(final float v1, final float v2) {
    	return max(new float[] { v1, v2 });
    }
    
    // Bit layout of "minus" zero.
    private static final long MINUS_ZERO_BIT_LAYOUT = Double.doubleToRawLongBits(-0.0);

    /**
     * Returns whether the given value is equal to "minus" zero.
     * 
     * @param value
     *           a value to check
     * @return true if the given value is equal to "minus" zero
     */
    public static boolean isMinusZero(final double value) {
    	return (Double.doubleToRawLongBits(value) == MINUS_ZERO_BIT_LAYOUT);
    }
    
    /**
     * Creates and returns an array of integer numbers.
     * 
     * @param len
     *           length of array
     * @return an array of integer numbers
     */
	public static int[] toIntArray(final int len) {
		int[] array = new int[len];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = ii;
		}
		return array;
	}

    /**
     * Creates and returns an array of double numbers.
     * 
     * @param len
     *           length of array
     * @return an array of double numbers
     */
	public static double[] toDoubleArray(final int len) {
		double[] array = new double[len];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = ii;
		}
		return array;
	}

}
