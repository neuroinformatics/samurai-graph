package jp.riken.brain.ni.samuraigraph.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


/**
 * A class for date.
 * 
 */
public class SGDate implements Comparable<SGDate> {
	
    // An input text string.
	private String mString = null;

	// Date format of a given text string.
    private DateFormat mFormat = null;

    // Time zone obtained from a given text string if it exists.
    // If time zone is not explicitly given, UTC time zone is assigned.
    private TimeZone mTimeZone = null;

    // The date and time.
    private DateTime mDateTime = null;

    /**
     * Array of default available date format.
     */
	private static final String[] DEFAULT_DATE_FORMAT_ARRAY = { 
		"yy/MM/dd", "yy.MM.dd", "yy MM dd" };

    /**
     * Note: The order of elements with no spaces is important.
     */
	private static final String[] NO_SEPARATOR_DEFAULT_DATE_FORMAT_ARRAY = { 
		"yyyyMMdd", "yyMMdd", "yyyy" };
	
	private static final String[] HYPHEN_DATE_FORMAT_ARRAY = {
		"yy-MM-dd", "yyyy-MM" };

    // list of date format
    private static final List<DateFormat> DATE_FORMAT_LIST = new ArrayList<DateFormat>();

    // list of date format with no separators
    private static final List<DateFormat> NO_SEPARATOR_DATE_FORMAT_LIST = new ArrayList<DateFormat>();

    // list of date format with hyphen separators
    private static final List<DateFormat> HYPHEN_DATE_FORMAT_LIST = new ArrayList<DateFormat>();

    private static final Pattern JODA_TIME_ZONE_PATTERN;
    
    // static initializer
    static {
        final Calendar cal = SGDateUtility.getUTCCalendarInstacne();
        for (String f : DEFAULT_DATE_FORMAT_ARRAY) {
        	DateFormat format = new SimpleDateFormat(f);
            format.setCalendar(cal);
            DATE_FORMAT_LIST.add(format);
        }
        for (String f : NO_SEPARATOR_DEFAULT_DATE_FORMAT_ARRAY) {
        	DateFormat format = new SimpleDateFormat(f);
            format.setCalendar(cal);
            NO_SEPARATOR_DATE_FORMAT_LIST.add(format);
        }
        for (String f : HYPHEN_DATE_FORMAT_ARRAY) {
        	DateFormat format = new SimpleDateFormat(f);
            format.setCalendar(cal);
            HYPHEN_DATE_FORMAT_LIST.add(format);
        }
        
		String regex = "T(.*)(\\+|-)\\d+:?\\d+$";
		JODA_TIME_ZONE_PATTERN = Pattern.compile(regex);
    };

	/**
	 * Builds a date object with a given text string.
	 * 
	 * @param str
	 *            a text string
	 * @throws ParseException
	 *             for invalid format
	 */
    public SGDate(final String str) throws ParseException {
        super();
        this.init(str);
    }
    
    private void init(String str) throws ParseException {
        final DateParseResult result = this.getDate(str);
        if (result == null) {
            throw new ParseException("Parse error: " + str, 0);
        }
        this.mString = str;
        this.mFormat = result.getFormat();
        
        DateTime resultDateTime = result.getDateTime();
        if (resultDateTime != null) {
            this.mDateTime = result.getDateTime();
            this.mTimeZone = result.getTimeZone();
        } else {
            this.mDateTime = new DateTime(result.getDate());
        	this.mTimeZone = SGDateUtility.getUTCTimeZoneInstance();
        }
    }
    
    /**
     * Creates an instance at the epoch time in UTC time zone.
     * 
     */
    public SGDate() {
    	this(0L);
    }
    
    public SGDate(DateTime dateTime) {
    	super();
    	this.mDateTime = dateTime;
    	this.mTimeZone = dateTime.getZone().toTimeZone();
    	this.mString = dateTime.toString();
    	this.mFormat = null;
    }
    
    public SGDate(final long millis) {
    	super();
    	this.mTimeZone = SGDateUtility.getUTCTimeZoneInstance();
    	this.mDateTime = SGDateUtility.toDateTime(millis, this.mTimeZone);
    	this.mString = this.mDateTime.toString();
    	this.mFormat = null;
    }
    
    public SGDate(final double dateValue) {
    	this(SGDateUtility.toMillis(dateValue));
    }

    public DateFormat getFormat() {
    	return this.mFormat;
    }
    
    /**
     * Returns the time zone of this date.
     * 
     * @return the time zone of this date
     */
    public TimeZone getTimeZone() {
    	return this.mTimeZone;
    }

    /**
     * Returns a DateTime object in UTC time zone.
     * 
     * @return a DateTime object in UTC time zone
     */
    public DateTime getUTCDateTime() {
    	return this.mDateTime;
    }

    /**
     * Returns a DateTime object in given time zone.
     * 
     * @param timeZone
     *          the time zone
     * @return a DateTime object in given time zone
     */
    public DateTime getDateTime(TimeZone timeZone) {
		return this.mDateTime.toDateTime(DateTimeZone.forTimeZone(timeZone));
    }

    /**
     * Returns a DateTime object in the time zone of this date.
     * 
     * @return a DateTime object in the time zone of this date
     */
    public DateTime getDateTime() {
		return this.getDateTime(this.mTimeZone);
    }

    @Override
    public String toString() {
        return this.mString;
    }
    
    /**
     * Returns a Calendar object for this date.
     * 
     * @return a Calendar object for this date
     */
    public Calendar getCalendar() {
    	// get a Calendar object with the time zone of this date object
        final Calendar cal = Calendar.getInstance(this.mTimeZone);
        
        // sets the date
    	cal.setTime(this.mDateTime.toDate());
        return cal;
    }
    
    /**
     * Returns this Calendar's time value in milliseconds. 
     * 
     * @return the current time as UTC milliseconds from the epoch.
     */
    public long getTimeInMillis() {
    	return this.getCalendar().getTimeInMillis();
    }
    
    /**
     * Returns this Calendar's time value in days. 
     *
     * @return the current time as UTC days from the epoch.
     */
    public double getDateValue() {
    	return SGDateUtility.toDateValue(this.getTimeInMillis());
    }

    public String toStringByMillis() {
    	return SGDateUtility.toStringByMillis(
    			this.getTimeInMillis(), this.getTimeZone());
    }

    public String toStringByMillis(TimeZone zone) {
    	return SGDateUtility.toString(this.getTimeInMillis(), zone);
    }

    public String toStringByDateValue() {
    	return SGDateUtility.toStringByDateValue(
    			this.getDateValue(), this.getTimeZone());
    }

    public String toStringByDateValue(TimeZone zone) {
    	return SGDateUtility.toString(this.getDateValue(), zone);
    }

	@Override
	public int compareTo(SGDate date) {
		return this.mDateTime.compareTo(date.getUTCDateTime());
	}

    private static class DateParseResult {
		private Date mDate;
		private DateFormat mFormat;
		private TimeZone mTimeZone;
    	
    	// for Joda Time library
    	private DateTime mDateTime;
    	
    	public DateParseResult(Date date, DateFormat format) {
    		super();
    		if (date == null || format == null) {
    			throw new IllegalArgumentException(
    					"date == null || format == null");
    		}
    		this.mDate = date;
    		this.mFormat = format;
    		
    		// UTC time zone is assigned
    		this.mTimeZone = SGDateUtility.getUTCTimeZoneInstance();
    	}
    	
    	public DateParseResult(DateTime dateTime, TimeZone timeZone) {
    		super();
    		if (dateTime == null || timeZone == null) {
    			throw new IllegalArgumentException("dateTime == null || timeZone == null");
    		}
    		this.mDateTime = dateTime;
    		this.mTimeZone = timeZone;
    	}

		public Date getDate() {
			return this.mDate;
		}

		public DateFormat getFormat() {
			return this.mFormat;
		}

		public DateTime getDateTime() {
			return this.mDateTime;
		}

		public TimeZone getTimeZone() {
			return this.mTimeZone;
		}
    }

    private DateParseResult getDate(final String str) {
    	DateParseResult ret = null;
    	
    	char[] cArray = str.toCharArray();
    	int hyphenCnt = 0;
    	for (char c : cArray) {
    		if (c == '-') {
    			hyphenCnt++;
    		}
    	}
    	
    	if (hyphenCnt != 0) {
    		// particular case of ISO8601
    		// YYYY-DDD
    		String regex = "\\d\\d\\d\\d-\\d\\d\\d";
    		Pattern p = Pattern.compile(regex);
    		Matcher m = p.matcher(str);
    		if (m.find()) {
        		ret = parseDateWithJodaTime(str);
            	if (ret != null) {
            		return ret;
            	}
    		}
    		
        	// parses a given string using a DateFormat object
        	// with hyphen separators
            for (DateFormat df : HYPHEN_DATE_FORMAT_LIST) {
            	ret = parseDate(str, df);
            	if (ret != null) {
            		break;
            	}
            }
        	if (ret != null) {
        		return ret;
        	}

        	// parse using Joda Time library
    		ret = parseDateWithJodaTime(str);
        	if (ret != null) {
        		return ret;
        	}

    	} else {
        	// parse using DateFormat objects with no separators
            for (DateFormat df : NO_SEPARATOR_DATE_FORMAT_LIST) {
            	ret = parseDate(str, df);
            	if (ret != null) {
            		break;
            	}
            }
        	if (ret != null) {
        		return ret;
        	}

        	// parse using Joda Time library
    		ret = parseDateWithJodaTime(str);
        	if (ret != null) {
        		return ret;
        	}

        	// parse using DateFormat objects
            if (ret == null) {
                for (DateFormat df : DATE_FORMAT_LIST) {
                	ret = parseDate(str, df);
                	if (ret != null) {
                		break;
                	}
                }
            }
        	if (ret != null) {
        		return ret;
        	}
    	}

        return ret;
    }
    
    private DateParseResult parseDateWithJodaTime(String str) {
    	DateParseResult ret = null;

    	// parses using Joda-Time library
		DateTime dateTime = null;
		DateTimeZone zone = null;
		try {
			// parses a text string with default settings
			dateTime = DateTime.parse(str);
			if (dateTime != null) {
				// gets the time zone from the DateTime object with default settings
				zone = dateTime.getZone();
				final int rawOffset = zone.toTimeZone().getRawOffset();
				if (rawOffset != 0) {
					// parses a text string with the parser in UTC time zone
					DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser()
							.withZoneUTC();
					dateTime = DateTime.parse(str, dtf);
					
					// if a given text string for date does not have 
					// the time zone, sets the UTC time zone
					Matcher m = JODA_TIME_ZONE_PATTERN.matcher(str);
					if (!m.find()) {
						zone = DateTimeZone.forTimeZone(
								SGDateUtility.getUTCTimeZoneInstance());
					}
				}
			}
			// Note: The DateTime object is always parsed in UTC time zone.
		} catch (IllegalArgumentException e) {
		}
		if (dateTime != null && zone != null) {
			ret = new DateParseResult(dateTime, zone.toTimeZone());
		}
		
    	return ret;
    }
    
    private DateParseResult parseDate(String str, DateFormat df) {
    	DateParseResult ret = null;
        ParsePosition pos = new ParsePosition(0);
        Date d = df.parse(str, pos);
        if (d == null) {
        	return ret;
        }
        if (pos.getIndex() == str.length()) {
            if (d != null) {
            	ret = new DateParseResult(d, df);
            }
        }
        return ret;
    }    
}
