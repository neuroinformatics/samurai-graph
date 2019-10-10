package jp.riken.brain.ni.samuraigraph.base;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SGDateUtility {

    // The factor for translation of date and its value.
    private static final long DATE_FACTOR = 1000 * 3600 * 24;

    /**
     * Transforms a given millisecond value to a date value.
     * 
     * @param millis
     *          a millisecond value
     * @return a date value
     */
    public static double toDateValue(final long millis) {
    	return (double) millis / DATE_FACTOR;
    }

    /**
     * Transforms a given date value to a millisecond value.
     * 
     * @param dateValue
     *          a date value
     * @return a millisecond value
     */
    public static long toMillis(final double dateValue) {
    	return (long) (dateValue * DATE_FACTOR);
    }
    
    public static String toStringByMillis(final long millis, TimeZone zone) {
    	return toString(millis, zone);
    }

    public static String toStringByDateValue(final double value, TimeZone zone) {
    	return toString(value, zone);
    }
    
    public static DateTime toDateTime(final long millis, TimeZone zone) {
        final Calendar cal = Calendar.getInstance(zone);
        cal.setTimeInMillis(millis);
        Date date = cal.getTime();
        DateTime dateTime = new DateTime(date.getTime(), 
        		DateTimeZone.forTimeZone(zone));
        return dateTime;
    }

    /**
     * Returns a text string for a given value with given time zone.
     *
     * @param value
     *           a value for date
     * @param zone
     *           time zone
     * @return a text string for date
     */
    public static String toString(final double value, TimeZone zone) {
    	return toString(SGDateUtility.toMillis(value), zone);
    }
    
    /**
     * Returns a text string for a given millisecond time with given time zone.
     *
     * @param millis
     *           a millisecond time for date
     * @param zone
     *           time zone
     * @return a text string for date
     */
    public static String toString(final long millis, TimeZone zone) {
        DateTime dateTime = toDateTime(millis, zone);
        return dateTime.toString();
    }

    public static Period toPeriodOfDays(final double dateValue) {
    	final long millisAll = SGDateUtility.toMillis(dateValue);
        final long secondsAll = millisAll / 1000L;
        final long minutesAll = secondsAll / 60L;
        final long hoursAll = minutesAll / 60L;
        final long daysAll = hoursAll / 24L;
        
        final int millis = (int) (millisAll - 1000L * secondsAll);
        final int seconds = (int) (secondsAll - 60L * minutesAll);
        final int minutes = (int) (minutesAll - 60L * hoursAll);
        final int hours = (int) (hoursAll - 24L * daysAll);
        final int days = (int) (daysAll);

        Period p = Period.days(days).plusHours(hours).plusMinutes(minutes)
        			.plusSeconds(seconds).plusMillis(millis);
        return p;
    }

    public static double toApproximateDateValue(Period p) {
    	double dMillis = 0.0;
    	double factor = 1.0;
    	dMillis += p.getMillis();
    	factor *= 1000;
    	dMillis += factor * p.getSeconds();
    	factor *= 60;
    	dMillis += factor * p.getMinutes();
    	factor *= 60;
    	dMillis += factor * p.getHours();
    	factor *= 24;
    	dMillis += factor * p.getDays();
    	factor *= 30;
    	dMillis += factor * p.getMonths();
    	factor *= 12;
    	dMillis += factor * p.getYears();
    	final long millis = (long) dMillis;
    	return SGDateUtility.toDateValue(millis);
    }

    /**
     * Sets a text string of ISO8601 format obtained from a given date value
     * to a text field.
     * 
     * @param tf
     *          a text field
     * @param obj
     *          the date value
     * @param zone
     *          time zone
     * @return true if succeeded
     */
    public static boolean setDateValue(final SGTextField tf, 
    		final Object obj, TimeZone zone) {
        if (obj == null) {
            tf.setText(null);
        } else {
            String valueStr = obj.toString().trim();
            SGDate date = null;
            try {
				date = new SGDate(valueStr);
			} catch (ParseException e) {
			}
            String str;
            if (date != null) {
                str = date.toStringByDateValue(zone);
            } else {
            	Double dValue = SGUtilityText.getDouble(valueStr);
            	if (dValue == null) {
            		return false;
            	}
            	str = SGDateUtility.toStringByDateValue(dValue, zone);
            }
            tf.setText(str);
        }
        return true;
    }

    public static boolean setPeriodValue(final SGTextField tf, 
    		final Object obj) {
        if (obj == null) {
            tf.setText(null);
        } else {
            String valueStr = obj.toString().trim();
    		Period p = SGUtilityText.getPeriod(valueStr);
            if (p == null) {
            	return false;
            }
            String str = p.toString();
            tf.setText(str);
        }
        return true;
    }

    /**
     * Returns an instance of UTC TimeZone object.
     * 
     * @return an instance of UTC TimeZone object
     */
    public static TimeZone getUTCTimeZoneInstance() {
    	return TimeZone.getTimeZone("UTC");
    }

    /**
     * Returns an instance of UTC Calendar object.
     * 
     * @return an instance of UTC Calendar object
     */
    public static Calendar getUTCCalendarInstacne() {
        return Calendar.getInstance(getUTCTimeZoneInstance());
    }

    /**
     * Returns a formatted string of date using the specified format string.
     * 
     * @param str
     *           a string for date
     * @param pattern
     *           format string
     * @return a formatted string of date
     */
	public static String format(String str, String pattern) {
		SGDate date = SGUtilityText.getDate(str);
		if (date == null) {
			return str;
		}
		if ("".equals(pattern)) {
			return str;
		}
		DateTime dt = date.getDateTime();
		String ret = str;
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
			ret = fmt.print(dt);
		} catch (Exception e) {
		}
		return ret;
	}

    public static void convertDateValue(SGTextField tf, final boolean toDateMode) {
    	String str = tf.getText();
    	if (str == null) {
    		return;
    	}
    	String strNew = str;
    	if (toDateMode) {
    		Double d = SGUtilityText.getDouble(str);
    		if (d == null) {
    			return;
    		}
    		SGDate date = new SGDate(d);
    		strNew = date.toString();
    	} else {
    		SGDate date = SGUtilityText.getDate(str);
    		if (date == null) {
    			return;
    		}
    		strNew = Double.toString(date.getDateValue());
    	}
    	tf.setText(strNew);
    }

    public static void convertPeriodValue(SGTextField tf, final boolean toDateMode) {
    	String str = tf.getText();
    	if (str == null) {
    		return;
    	}
    	String strNew = str;
    	if (toDateMode) {
    		Double d = SGUtilityText.getDouble(str);
    		if (d == null) {
    			return;
    		}
    		Period p = SGDateUtility.toPeriodOfDays(d);
    		strNew = p.toString();
    	} else {
    		Period p = SGUtilityText.getPeriod(str);
    		if (p == null) {
    			return;
    		}
    		strNew = Double.toString(SGDateUtility.toApproximateDateValue(p));
    	}
    	tf.setText(strNew);
    }

    public static boolean isValidDateFormat(final String pattern) {
    	try {
        	DateTimeFormat.forPattern(pattern);
		} catch (Exception e) {
			return false;
    	}
    	return true;
    }
    
    public static boolean checkDateComboBoxInputValidatity(SGComboBox cb) {
        Object dateFormatItem = cb.getSelectedItem();
        if (dateFormatItem != null) {
        	String pattern = dateFormatItem.toString();
        	if (!"".equals(pattern)) {
                if (!SGDateUtility.isValidDateFormat(pattern)) {
                	return false;
                }
        	}
        }
        return true;
    }
}
