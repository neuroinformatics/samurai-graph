package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.print.attribute.standard.MediaSize;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;

import org.joda.time.Period;
import org.mozilla.universalchardet.UniversalDetector;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class with utility methods which are used to transform a text string into a
 * value such as number or color, and vice versa.
 * 
 */
public class SGUtilityText implements SGIConstants, SGIDrawingElementConstants,
        SGIPropertyFileConstants {

    /**
     * Create a string with superscript.
     * @param base
     *             a base string
     * @param sp
     *             a string for superscript
     */
    public static String getSuperscriptString(final String base, final String sp) {
		StringBuffer sb = new StringBuffer();
		sb.append(base);
		sb.append("^{");
		sb.append(sp);
		sb.append("}");
		return sb.toString();
    }

    /**
     * Create a string with subscript.
     * @param base
     *             a base string
     * @param sub
     *             a string for subscript
     */
    public static String getSubscriptString(final String base, final String sub) {
		StringBuffer sb = new StringBuffer();
		sb.append(base);
		sb.append("_{");
		sb.append(sub);
		sb.append("}");
		return sb.toString();
    }

    /**
     * Separates a given text string into the base strings, subscript strings and 
     * superscript strings.
     * 
     * @param line
     *            A line.
     * @param bseList
     *            List of base strings.
     * @param superList
     *            List of superscript strings.
     * @param subList
     *            List of subscript strings.
     * @return true if succeeded
     */
    public static boolean getSubscriptAndSuperscriptInfo(final String line,
            final List<String> baseList, final List<String> superList,
            final List<String> subList) {

        if (line == null || baseList == null || superList == null
                || subList == null) {
            return false;
        }
        if (line.length() == 0) {
            return false;
        }

        // working variables
        boolean in_super = false;
        boolean in_sub = false;
        boolean in_index_brace = false;
        boolean in_escape = false;
        int in_base_brace_stack = 0;
        int in_brace_stack = 0;
        StringBuffer cache_base = new StringBuffer();
        StringBuffer cache_super = new StringBuffer();
        StringBuffer cache_sub = new StringBuffer();
        for (int ii = 0; ii < line.length(); ii++) {
            final char c = line.charAt(ii);
            if (in_super || in_sub) {
                if (in_index_brace) {
                    if (in_escape) {
                        in_escape = false;
	                    if (c == '{' || c == '}') {
                            if (in_super) {
                                cache_super.append('\\');
                                cache_super.append(c);
                            } else {
                            	cache_sub.append('\\');
                                cache_sub.append(c);
                            }
	                    	continue;
	                    }
                        if (in_super) {
                        	MatchingResultSub result = matches(line, ii, cache_super, c);
                        	ii = result.index;
                        	if (result.bBreak) {
                        		break;
                        	}
                        } else {
                        	MatchingResultSub result = matches(line, ii, cache_sub, c);
                        	ii = result.index;
                        	if (result.bBreak) {
                        		break;
                        	}
                        }
                    } else {
                        if (c == '\\') {
                            in_escape = true;
                        } else {
                            if (c == '}' && in_brace_stack == 0) {
                                in_index_brace = false;
                                in_super = false;
                                in_sub = false;
                            } else {
                                if (c == '}') {
                                    in_brace_stack--;
                                } else if (c == '{') {
                                    in_brace_stack++;
                                }
                                if (in_super) {
                                    cache_super.append(c);
                                } else {
                                    cache_sub.append(c);
                                }
                            }
                        }
                    }
                } else if (c == '{') {
                    in_index_brace = true;
                    in_brace_stack = 0;
                } else {
                    if (in_escape) {
                        in_escape = false;
	                    if (c == '{' || c == '}') {
	                    	cache_base.append(c);
	                    	continue;
	                    }
                        if (in_super) {
                            in_super = false;
                        	MatchingResultSub result = matches(line, ii, cache_super, c);
                        	ii = result.index;
                        	if (result.bBreak) {
                        		break;
                        	}
                        } else {
                            in_sub = false;
                        	MatchingResultSub result = matches(line, ii, cache_sub, c);
                        	ii = result.index;
                        	if (result.bBreak) {
                        		break;
                        	}
                        }
                    } else {
                        if (c == '\\') {
                            in_escape = true;
                        } else {
                            if (in_super) {
                                cache_super.append(c);
                                in_super = false;
                            } else {
                                cache_sub.append(c);
                                in_sub = false;
                            }
                        }
                    }
                }
            } else {
            	// in base
            	if (in_escape) {
                    in_escape = false;
                    if (c == '{' || c == '}') {
						initCaches(cache_base, cache_super, cache_sub,
								baseList, superList, subList);
                    	cache_base.append(c);
                    	continue;
                    }
					initCaches(cache_base, cache_super, cache_sub, baseList,
							superList, subList);
                	MatchingResultSub result = matches(line, ii, cache_base, c);
                	ii = result.index;
                	if (result.bBreak) {
                		break;
                	}
            	} else {
                    if (c == '^') {
                        if (cache_base.length() == 0 || cache_super.length() != 0) {
                            return false;
                        }
                        in_super = true;
                    } else if (c == '_') {
                        if (cache_base.length() == 0 || cache_sub.length() != 0) {
                            return false;
                        }
                        in_sub = true;
                    } else if (c == '\\') {
                        in_escape = true;
                    } else if (c == '{') {
                    	in_base_brace_stack++;
                    } else if (c == '}') {
                    	in_base_brace_stack--;
                    } else {
						initCaches(cache_base, cache_super, cache_sub,
								baseList, superList, subList);
                    	cache_base.append(c);
                    }
            	}
            }
            
        }
        if (in_super || in_sub || in_index_brace || in_escape || in_base_brace_stack != 0) {
            return false;
        }
        if (cache_base.length() != 0) {
			// flushes the caches
			flushCaches(cache_base, cache_super, cache_sub, baseList,
					superList, subList);
        }
        return true;
    }
    
    private static class MatchingResultSub extends MatchingResult {
    	int index = -1;
    	boolean bBreak = false;
    	MatchingResultSub(MatchingResult result) {
    		super(result);
    	}
    }
    
	private static MatchingResultSub matches(final String line, final int index,
			final StringBuffer cache, final char c) {
		MatchingResult matchingResult = findPattern(line, index);
		MatchingResultSub result = new MatchingResultSub(matchingResult);
		if (matchingResult.result != null) {
			cache.append(matchingResult.result);
			result.index = matchingResult.end - 1;
		} else {
			cache.append(c);
			result.index = index;
		}
		result.bBreak = (matchingResult.end == line.length());
		return result;
	}
    
	// Initializes the caches.
	private static void initCaches(StringBuffer cache_base,
			StringBuffer cache_super, StringBuffer cache_sub,
			List<String> baseList, List<String> superList, List<String> subList) {
		if (cache_super.length() != 0 || cache_sub.length() != 0) {
			// flushes the caches
			flushCaches(cache_base, cache_super, cache_sub, baseList,
					superList, subList);
			
			// resets the caches
			cache_base.setLength(0);
			cache_super.setLength(0);
			cache_sub.setLength(0);
		}
	}

	// Flushes the caches.
	private static void flushCaches(StringBuffer cache_base,
			StringBuffer cache_super, StringBuffer cache_sub,
			List<String> baseList, List<String> superList, List<String> subList) {
		baseList.add(cache_base.toString());
		if (cache_super.length() != 0) {
			superList.add(cache_super.toString());
		} else {
			superList.add(null);
		}
		if (cache_sub.length() != 0) {
			subList.add(cache_sub.toString());
		} else {
			subList.add(null);
		}
	}

    /**
     * Complies a given text string.
     * 
     * @param str
     *           a text string
     * @return compiled result
     */
    public static String compile(final String str) {
    	StringBuffer sb = new StringBuffer();
        boolean in_escape = false;
        for (int ii = 0; ii < str.length(); ii++) {
            final char c = str.charAt(ii);
            if (in_escape) {
                if (c == '\\') {
                	// two successive escape characters
                	sb.append(c);
                } else if (c == '{' || c == '}') {
                	sb.append(c);
                } else {
                	MatchingResult matchingResult = findPattern(str, ii);
                	if (matchingResult.result != null) {
                		sb.append(matchingResult.result);
                		if (matchingResult.end == str.length()) {
                            in_escape = false;
                			break;
                		}
                		ii = matchingResult.end - 1;
                	} else {
                    	// add the current character
                    	sb.append(c);
                	}
                }
                in_escape = false;
            } else {
                if (c == '\\') {
                    in_escape = true;
                } else if (c == '{' || c == '}') {
                	continue;
                } else {
                    sb.append(c);
                }
            }
        }
    	return sb.toString();
    }
    
    static class MatchingResult {
    	String input;
    	int start;
    	int end;
    	String result;
    	MatchingResult() {
    		super();
    	}
    	MatchingResult(MatchingResult res) {
    		this();
    		this.input = res.input;
    		this.start = res.start;
    		this.end = res.end;
    		this.result = res.result;
    	}
    }

    private static MatchingResult findPattern(final String str, final int start) {
    	// searches the candidate
    	StringBuffer sb = new StringBuffer();
    	int end = str.length();
    	if (start < str.length()) {
        	for (int ii = start; ii < str.length(); ii++) {
                final char c = str.charAt(ii);
                if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
            		sb.append(c);
                } else {
                	end = ii;
                	break;
                }
        	}
    	}
    	String result = matches(sb.toString());
    	MatchingResult ret = new MatchingResult();
    	ret.input = str;
    	ret.start = start;
    	ret.end = end;
    	ret.result = result;
    	return ret;
    }
    
    private static final String matches(final String str) {
    	Character c = SGStringPatterns.getChar(str);
    	if (c == null) {
    		return null;
    	} else {
    		return c.toString();
    	}
    }

    public static boolean tokenize(final String str, final List<Token> tokenList,
            final boolean isDataFile) {

        if (str == null || tokenList == null) {
            throw new IllegalArgumentException("str==null || tokenList==null");
        }

        if (str.length() == 0) {
            return true;
        }
        if (count(str, '"') % 2 != 0) {
            return false;
        }
        
        SGCSVTokenizer csvt = new SGCSVTokenizer(str, isDataFile);
        while (csvt.hasMoreTokens()) {
            try {
                Token result = csvt.nextToken();
                tokenList.add(result);
            } catch (NoSuchElementException e) {
            }
        }
        return true;
    }

    /**
     * 
     * @param charList
     * @return
     */
    public static String createString(final ArrayList<Character> charList) {
        final char[] cArray = new char[charList.size()];
        for (int ii = 0; ii < cArray.length; ii++) {
            cArray[ii] = charList.get(ii).charValue();
        }
        final String str = new String(cArray);
        return str;
    }

    /**
     * Read a line with finite length and with any character other than spaces.
     * 
     * @param br
     *           a buffered reader object
     * @return a line
     */
    public static String readLine(final BufferedReader br) {
        String line = null;
        try {
            while (true) {
            	// read a line
                line = br.readLine();
                
                // the end of file
                if (line == null) {
                	break;
                }

                // a line that contains only spaces
                boolean spacesOnly = true;
                char[] cArray = line.toCharArray();
                for (int ii = 0; ii < cArray.length; ii++) {
                	if (!Character.isSpaceChar(cArray[ii])) {
                		spacesOnly = false;
                		break;
                	}
                }
                if (spacesOnly) {
                	continue;
                }
                
                // skip byte-order mark
                if (line.length() != 0) {
                    if (line.startsWith(Character.toString(BOM_CHAR))) {
                    	line = line.substring(1, line.length());
                    }
                }
                
                // a line with finite length
                if (line.length() != 0) {
                    break;
                }
            }
        } catch (IOException ex) {
            return null;
        }
        return line;
    }

    /**
     * Create a string representation of a list of color objects 
     * in a format of "{(r1,g1,b1,a1),(r2,g2,b2,a2),...}".
     * @param colorList
     *                  a list of colors
     * @return
     *                  a string representation of a list of color objects
     */
    public static String getColorListString(final List<Color> colorList) {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
        for (int ii = 0; ii < colorList.size(); ii++) {
            final Color color = colorList.get(ii);
            final String strColor = SGUtilityText.getColorString(color);
            sb.append(strColor);
            if (ii != colorList.size() - 1) {
        	sb.append(',');
            } else {
        	sb.append('}');
            }
        }
        return sb.toString();
    }

    /**
     * Write the information of a color list in a format of
     * "key={(r1,g1,b1,a1),(r2,g2,b2,a2),...}" to the writer object.
     * @param writer
     *               a writer object
     * @param key
     *               the key
     * @param colorList
     *               the color list
     * @return
     *               true if succeeded
     */
    public static boolean writeColorListPropertyLine(final Writer writer,
            final String key, final List<Color> colorList) throws IOException {
        writer.write(key);
        writer.write("=");
        String str = getColorListString(colorList);
        writer.write(str);
        writer.write("\n");
        return true;
    }

    /**
     * Search the start character from the head of a given string
     * and the end character from the tail of the string, respectively.
     * If both of them are found, returns a string between them.
     * 
     * @param str
     *            a string
     * @param sChar
     *            the start character
     * @param eChar
     *            the end character
     * @return a substring if it exists, or null if not found
     */
    public static String getInnerString(final String str, final int sChar,
            final int eChar) {
        if (str == null) {
            return null;
        }
        if (str.length() < 2) {
            return null;
        }
        int start = str.indexOf(sChar) + 1;
        if (start == -1) {
            return null;
        }
        int end = str.lastIndexOf(eChar);
        if (end == -1) {
            return null;
        }
        if (end <= start) {
            return null;
        }
        String sub = str.substring(start, end);
        return sub;
    }
    
    /**
     * Search a open bracket from the head of a given string
     * and a closed bracket from the tail of the string, respectively.
     * If both of them are found, parses the text string between them 
     * and creates an array of text strings between a bracket.
     * Spaces on the both side of each string are removed.
     * 
     * @param str
     *           a text string
     * @return an array of text strings if parsing succeeded
     */
    public static String[] getStringsInBracket(final String str) {
        if (str == null) {
            return null;
        }
        String sub = SGUtilityText.getInnerString(str, '(', ')');
        if (sub == null) {
            return null;
        }
        String[] sArray = sub.split(",");
        for (int ii = 0; ii < sArray.length; ii++) {
        	sArray[ii] = sArray[ii].trim();
        }
        return sArray;
    }

    /**
     * Parses a given text string and creates an array of integers.
     * 
     * @param str
     *           a text string
     * @return an array of integer if parsing succeeded
     */
    public static int[] getIntegerArray(final String str) {
        if (str == null) {
            return null;
        }
        String[] strArray = getStringsInBracket(str);
        if (strArray == null) {
            return null;
        }

        int[] array = new int[strArray.length];
        for (int ii = 0; ii < array.length; ii++) {
            Integer num = SGUtilityText.getInteger(strArray[ii]);
            if (num == null) {
                return null;
            }
            array[ii] = num.intValue();
        }

        return array;
    }
    
    /**
     * Parses a given text string and creates an array of float numbers.
     * 
     * @param str
     *           a text string
     * @return an array of float numbers if parsing succeeded
     */
    public static float[] getFloatArray(final String str) {
        if (str == null) {
            return null;
        }
        String[] strArray = getStringsInBracket(str);
        if (strArray == null) {
            return null;
        }

        float[] array = new float[strArray.length];
        for (int ii = 0; ii < array.length; ii++) {
            Float num = SGUtilityText.getFloat(strArray[ii]);
            if (num == null) {
                return null;
            }
            array[ii] = num.floatValue();
        }

        return array;
    }

    /**
     * Parses a given text string and creates an array of double numbers.
     * 
     * @param str
     *           a text string
     * @return an array of double numbers if parsing succeeded
     */
    public static double[] getDoubleArray(final String str) {
        if (str == null) {
            return null;
        }
        String[] strArray = getStringsInBracket(str);
        if (strArray == null) {
            return null;
        }

        double[] array = new double[strArray.length];
        for (int ii = 0; ii < array.length; ii++) {
            Double num = SGUtilityText.getDouble(strArray[ii]);
            if (num == null) {
                return null;
            }
            array[ii] = num.doubleValue();
        }

        return array;
    }

    /**
     * Parses a given text string in a format of "(r,g,b)" or "(r,g,b,a)" 
     * and creates a color object.
     * 
     * @param str
     *           a text string
     * @return a color object if parsing succeeds
     */
    public static Color parseColor(final String str) {
        final int[] array = getIntegerArray(str);
        if (array == null) {
            return null;
        }
        final int len = array.length;
        if (len != 4 && len != 3) {
            return null;
        }
        for (int ii = 0; ii < len; ii++) {
            if (array[ii] < 0 || array[ii] > 255) {
                return null;
            }
        }

        Color color = null;
        if (len == 3) {
            color = new Color(array[0], array[1], array[2]);
        } else if (len == 4) {
            color = new Color(array[0], array[1], array[2], array[3]);
        }

        return color;
    }

    /**
     * Create a string representation of a color object from a given string
     * in a format of "(r,g,b,a)".
     * @param color
     *              a color object
     * @return
     *              a string representation of a given color
     */
    public static String getColorString(final Color color) {
		if (color == null) {
		    return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append(color.getRed());
        sb.append(',');
        sb.append(color.getGreen());
        sb.append(',');
        sb.append(color.getBlue());
//        sb.append(',');
//        sb.append(color.getAlpha());
        sb.append(')');
        return sb.toString();
    }

    /**
     * Creates a string representation of a color object from a given string
     * in a format of "r, g, b".
     * 
     * @param color
     *              a color object
     * @return a string representation of a given color
     */
    public static String getSimpleColorString(Color cl) {
        if (cl != null) {
            StringBuffer sb = new StringBuffer();
            sb.append(cl.getRed());
            sb.append(", ");
            sb.append(cl.getGreen());
            sb.append(", ");
            sb.append(cl.getBlue());
            return sb.toString();
        } else {
        	return null;
        }
    }

    /**
     * 
     * @param value
     * @return Boolean instance. null if failed.
     */
    public static Boolean getBoolean(final String value) {
        if (value == null) {
            return null;
        }
        Boolean b = null;
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            b = Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            b = Boolean.FALSE;
        }
        return b;
    }

    /**
     * 
     */
    public static Integer getInteger(final String value) {
        Number num = parse(value);
        if (num == null) {
            return null;
        }
        return Integer.valueOf(num.intValue());
    }

    /**
     * 
     */
    public static Float getFloat(final String value) {
        Number num = parse(value);
        if (num == null) {
            return null;
        }
        return Float.valueOf(num.floatValue());
    }
    
    /**
     * Parse a text string and returns a double-precision floating-point number.
     */
    public static Double getDouble(final String value) {
        if (value == null) {
            return null;
        }
        if (SGITextDataConstants.NOT_A_NUMBER_VALUE.equalsIgnoreCase(value)) {
            return Double.NaN;
        }
        Number num = parse(value);
        if (num == null) {
            return null;
        }
        return Double.valueOf(num.doubleValue());
    }

    /**
     * Create a string in a CVS format from a given string separated with spaces or tabs.
     * @param value
     *              a given string
     * @return
     *              a string in a CVS format
     */
    public static String getCSVString(final String value) {
		if (value == null) {
		    return null;
		}
        final String space = " ";
        final String tab = "\t";
        boolean use_quote = false;
        StringBuffer sb = new StringBuffer();
        for (int ii = 0; ii < value.length(); ii++) {
            char c = value.charAt(ii);
            if (c == ',') {
                use_quote = true;
            } else if (c == '"') {
                use_quote = true;
                sb.append('"');
            }
            sb.append(c);
        }
        if (value.startsWith(space) || value.startsWith(tab)
                || value.endsWith(space) || value.endsWith(tab)) {
            use_quote = true;
        }
        if (use_quote) {
            String str = sb.toString();
            sb.append("\"");
            sb.append(str);
            sb.append("\"");
        }
        return sb.toString();
    }

    /**
     * Parses given text string and creates a list of tokens.
     * 
     * @param line
     *           a text string
     * @return a list of tokens
     */
    public static List<String> getTokenList(final String line) {
        String sub = SGUtilityText.getInnerString(line, '{', '}');
        if (sub == null) {
            return null;
        }
        String[] tokens = sub.split(",");
        List<String> strList = new ArrayList<String>();
        for (String token : tokens) {
        	strList.add(token);
        }
        return strList;
    }

    /**
     * Parses given text string and creates a list of colors.
     * 
     * @param line
     *           a text string
     * @return a list of colors
     */
    public static List<Color> getColorList(final String line) {
        String sub = SGUtilityText.getInnerString(line, '{', '}');
        if (sub == null) {
            return null;
        }
        List<String> strColorList = SGUtilityText
                .getTokenListInBracket(sub);
        ArrayList<Color> colorList = new ArrayList<Color>();
        for (int ii = 0; ii < strColorList.size(); ii++) {
            String strColor = (String) strColorList.get(ii);
            Color color = SGUtilityText.parseColor(strColor);
            colorList.add(color);
        }
        return colorList;
    }

    /**
     * Creates a list of tokens from a given text string separated with commas such as
     * (str1), (str2), ..., (strN).
     * 
     * @param line
     *           a text string
     * @return a list of tokens
     */
    public static List<String> getTokenListInBracket(String line) {
        int fromIndex = 0;
        int begin;
        int end;
        ArrayList<String> strList = new ArrayList<String>();
        while (true) {
            begin = line.indexOf("(", fromIndex);
            end = line.indexOf(")", fromIndex);
            if (begin >= end) {
                break;
            }
            String sub = line.substring(begin, end + 1);
            if (sub == null) {
                break;
            }
            strList.add(sub);
            fromIndex = end + 1;
        }
        return strList;
    }

    /**
     * Returns whether a given font style is valid.
     * 
     * @param style
     *           a font style
     * @return true if the given font style is valid
     */
    public static boolean isValidFontStyle(final int style) {
        final int[] array = {
                Font.PLAIN, Font.BOLD, Font.ITALIC, (Font.BOLD | Font.ITALIC)
        };
        for (int ii = 0; ii < array.length; ii++) {
            if (style == array[ii]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the name of a given font style.
     * 
     * @param style
     *           a font style
     * @return the name of a given font style
     */
    public static String getFontStyleName(final int style) {
        String name = null;
        switch (style) {
        case Font.PLAIN:
            name = FONT_PLAIN;
            break;
        case (Font.BOLD | Font.ITALIC):
            name = FONT_BOLD_ITALIC;
            break;
        case Font.BOLD:
            name = FONT_BOLD;
            break;
        case Font.ITALIC:
            name = FONT_ITALIC;
        default:
        }
        return name;
    }

    /**
     * Returns the font style from a given name.
     * 
     * @param name
     *           the name of a font style
     * @return the font style
     */
    public static Integer getFontStyle(final String name) {
        if (name == null) {
            return null;
        }
        Integer style = null;
        if (SGUtilityText.isEqualString(FONT_PLAIN, name)) {
        	style = Font.PLAIN;
        } else if (SGUtilityText.isEqualString(FONT_BOLD, name)) {
            style = Font.BOLD;
        } else if (SGUtilityText.isEqualString(FONT_ITALIC, name)) {
            style = Font.ITALIC;
        } else if (SGUtilityText.isEqualString(FONT_BOLD_ITALIC, name)) {
            style = Font.BOLD | Font.ITALIC;
        }
        return style;
    }

    /**
     * Returns the name of given scale type.
     * 
     * @param type
     *           scale type
     * @return the name of given scale type
     */
    public static String getScaleTypeName(final int type) {
        String name = null;
        switch (type) {
        case SGAxis.LINEAR_SCALE:
            name = SGIConstants.SCALE_TYPE_LINEAR;
            break;
        case SGAxis.LOG_SCALE:
            name = SGIConstants.SCALE_TYPE_LOG;
            break;
        default:
       	}
        return name;
    }

    /**
     * Returns the scale type from given name.
     * 
     * @param name
     *           the name of scale type
     * @return the scale type
     */
    public static int getScaleType(final String name) {
        if (name == null) {
            return -1;
        }
        int type = -1;
        if (SCALE_TYPE_LINEAR.equalsIgnoreCase(name)) {
            type = SGAxis.LINEAR_SCALE;
        } else if (SCALE_TYPE_LOG.equalsIgnoreCase(name)) {
            type = SGAxis.LOG_SCALE;
        }
        return type;
    }

    /**
     * 
     * @param spec
     * @return
     */
    public static Document getDocument(String spec) {
        Document doc = null;

        // create an URL instance
        try {
            URL url = new URL(spec);
            doc = getDocument(url);
        } catch (MalformedURLException ex) {
            return null;
        }

        return doc;
    }

    /**
     * property entity resolver
     * 
     * @author okumura
     * 
     */
    private static class PropertyEntityResolver implements EntityResolver {
        public InputSource resolveEntity(final String publicId,
                final String systemId) throws SAXException, IOException {
            if (publicId.startsWith(PROPERTY_FILE_PUBLIC_ID)
                    && systemId.startsWith(PROPERTY_FILE_SYSTEM_ID)) {
                String name = SGIConstants.RESOURCES_DIRNAME
                        + PROPERTY_DTD_FILE_NAME;
                InputStream in = this.getClass().getResourceAsStream(name);
                return new InputSource(in);
            }
            return null;
        }
    }

    private static EntityResolver mPropertyEntityResolver = new PropertyEntityResolver();

    /**
     * Return Document object from given URL.
     * 
     * @param url URL of input stream
     * @return Document. null if failed.
     * @throws Exception
     */
    public static Document getDocument(URL url) {
        Document doc = null;
        InputStream bis = null;
        // get input stream
        try {
            bis = new BufferedInputStream(url.openStream());
        } catch (IOException ex) {
//            ex.printStackTrace();
            return null;
        }
        // parse input and create a Document object
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(SGUtilityText.mPropertyEntityResolver);
            doc = builder.parse(bis, url.toString());
        } catch (ParserConfigurationException e) {
        	return null;
		} catch (SAXException e) {
        	return null;
		} catch (IOException e) {
        	return null;
		} finally {
            // close the input stream
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException ex) {
            }
        }

        return doc;
    }
    
    /**
     * Return Document object which is converted given string to.
     * 
     * @param documentString XML string
     * @return Document. null if failed.
     */
    public static Document getDocumentFromString(String documentString) {
        Document doc = null;
        InputStream bis = null;
        // parse input and create a Document object
        try {
            bis = new BufferedInputStream(new ByteArrayInputStream(documentString.getBytes(SGIConstants.CHAR_SET_NAME_UTF8)));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(SGUtilityText.mPropertyEntityResolver);
            doc = builder.parse(bis);
        } catch (ParserConfigurationException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (SAXException ex) {
            return null;
        } finally {
            // close the input stream
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException ex) {
            }
        }
        
        return doc;
    }

    /**
     * 
     * @param str
     * @return
     */
    private static Number parse(final String str) {
        if (str == null) {
            return null;
        }

        String strNew = str;

        // remove the first character of '+'
        if (str.startsWith("+")) {
            if (str.length() > 1) {
                strNew = strNew.substring(1);
            }
        }

        // from e to E
        strNew = strNew.toUpperCase();

        // from E+ to E
        strNew = strNew.replaceAll("E\\+", "E");

        // parse the string
        ParsePosition pos = new ParsePosition(0);
        Number num = NumberFormat.getInstance().parse(strNew, pos);
        if (pos.getIndex() != strNew.length()) {
            return null;
        }

        return num;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static Number getLengthInPoint(final String str) {
        return getLength(str, pt);
    }

    /**
     * 
     * @param str
     * @return
     */
    public static Number getLength(final String str, final String unit) {
        if (str == null) {
            return null;
        }

        if (str.length() == 0) {
            return null;
        }

        final String ls = str.toLowerCase();
        final String[] units = getUnitsArrayOfLength();
        for (int ii = 0; ii < units.length; ii++) {
            if (ls.endsWith(units[ii])) {
                Number num = removeUnit(ls, units[ii]);
                if (num != null) {
                    return Double.valueOf(convert(num.doubleValue(), units[ii],
                            unit));
                }
            }
        }

        return null;
    }
    
    /**
     * 
     * @param str
     * @param unit
     * @return
     */
    public static Integer getInteger(final String str, final String unit) {
        Number num = SGUtilityText.removeUnit(str, unit);
        if (num == null) {
            return null;
        }
        return Integer.valueOf(num.intValue());
    }

    /**
     * 
     * @param str
     * @param unit
     * @return
     */
    public static Float getFloat(final String str, final String unit) {
        Number num = SGUtilityText.removeUnit(str, unit);
        if (num == null) {
            return null;
        }
        return Float.valueOf(num.floatValue());
    }

    /**
     * 
     * @param str
     * @param unit
     * @return
     */
    public static Double getDouble(final String str, final String unit) {
        Number num = SGUtilityText.removeUnit(str, unit);
        if (num == null) {
            return null;
        }
        return Double.valueOf(num.doubleValue());
    }

    /**
     * Check whether the string is valid.
     * 
     * @param str
     *            a string
     * @return whether the string is valid
     */
    public static boolean isValidString(final String str) {
        if (str == null) {
            return false;
        }

        final int len = str.length();

        // if the length of the string equals to zero
        if (len == 0) {
            return false;
        }

        // if the string consists of spaces
        boolean flag = false;
        for (int ii = 0; ii < len; ii++) {
            final char c = str.charAt(ii);
            if (c != ' ' && c != twoByteSpaceChar && c != '\t' && c != '\n'
                    && c != '\r' && c != '\f') {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return false;
        }

        return true;
    }

    /**
     * Creates sequentially-numbered name in a given name list.
     * 
     * @param list
     *             a name list
     * @param name
     *             a name
     * @return sequentially-numbered name
     */
    public static String getSerialName(List<String> list, final String name) {
        int max = -1;
        for (int ii = 0; ii < list.size(); ii++) {
            String str = list.get(ii);
            if (str.startsWith(name)) {
                String sub = str.substring(name.length());
                if (sub.startsWith("(") && sub.endsWith(")")) {
                    String sub2 = sub.substring(1, sub.length() - 1);
                    Integer index = SGUtilityText.getInteger(sub2);
                    if (index != null) {
                        int n = index.intValue();
                        if (n > max) {
                            max = n;
                        }
                    }
                } else if ("".equals(sub)) {
                    max = 0;
                }
            }
        }

        String nameNew;
        if (max != -1) {
            max++;
            StringBuffer sb = new StringBuffer();
            sb.append(name);
            sb.append('(');
            sb.append(max);
            sb.append(')');
            nameNew = sb.toString();
        } else {
            nameNew = name;
        }

        return nameNew;
    }

    /**
     * 
     * @return
     */
    public static String[] getUnitsArrayOfLength() {
        return new String[] { SGIConstants.cm, SGIConstants.mm,
                SGIConstants.pt, SGIConstants.inch };
    }

    /**
     * 
     * @param unit
     * @return
     */
    public static boolean isLengthUnit(final String unit) {
        if (unit == null) {
            throw new IllegalArgumentException();
        }
        final String[] array = SGUtilityText.getUnitsArrayOfLength();
        boolean flag = false;
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].equalsIgnoreCase(unit)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 
     * @param value
     * @param unitOld
     * @param unitNew
     * @return
     */
    public static double convert(final double value, final String unitOld,
            final String unitNew) {
        // check
        if (unitOld == null || unitNew == null) {
            throw new IllegalArgumentException();
        }

        if (!isLengthUnit(unitOld) || !isLengthUnit(unitNew)) {
            throw new IllegalArgumentException();
        }

//        final String uOld = unitOld.toLowerCase();
//        final String uNew = unitNew.toLowerCase();

        // no needs of conversion
        if (unitOld.equalsIgnoreCase(unitNew)) {
            return value;
        }

        final String[] units = SGUtilityText.getUnitsArrayOfLength();
        final float[] ratioArray = { 1.0f, 0.10f, CM_POINT_RATIO, CM_INCH_RATIO };
        int indexOld = -1;
        for (int ii = 0; ii < units.length; ii++) {
            if (units[ii].equalsIgnoreCase(unitOld)) {
                indexOld = ii;
                break;
            }
        }
        if (indexOld == -1) {
            throw new IllegalArgumentException();
        }
        int indexNew = -1;
        for (int ii = 0; ii < units.length; ii++) {
            if (units[ii].equalsIgnoreCase(unitNew)) {
                indexNew = ii;
                break;
            }
        }
        if (indexNew == -1) {
            throw new IllegalArgumentException();
        }

        final float ratio = ratioArray[indexOld] / ratioArray[indexNew];

        return value * ratio;
    }

    /**
     * 
     * @param value
     * @param unit
     * @return
     */
    public static double convertToPoint(final double value, final String unit) {
        return convert(value, unit, pt);
    }

    /**
     * 
     * @param value
     * @param unit
     * @return
     */
    public static double convertFromPoint(final double value, final String unit) {
        return convert(value, pt, unit);
    }

    /**
     * Parse and convert to the given unit. ex. str - 1 inch , unit - cm is
     * converted to "2.54"
     * 
     * @param str -
     *            a string to be parsed
     * @return parsed string
     */
    public static String convertString(final String str, final String unit) {
        if (str == null || unit == null) {
            return null;
        }
        if (str.length() == 0 || unit.length() == 0) {
            return null;
        }

        final String s = str.toLowerCase();
        final String u = unit.toLowerCase();

        // check whether the string ends with the suffix of unit
        String[] unitsArray = SGUtilityText.getUnitsArrayOfLength();
        String suffix = null;
        for (int ii = 0; ii < unitsArray.length; ii++) {
            if (str.endsWith(unitsArray[ii])) {
                suffix = unitsArray[ii];
                break;
            }
        }
        if (suffix == null) {
            return null;
        }

        Number value = SGUtilityText.removeUnit(s, suffix);
        if (value == null) {
            return null;
        }
        final double num = value.doubleValue();

        //
        final double numNew = SGUtilityText.convert(num, suffix, u);
        String valueNew = Double.toString(numNew);

        return valueNew;
    }

    /**
     * Remove the unit from a text string, parse and create a Number object.
     * 
     * @param str -
     *            a text string to be parsed
     * @param unit -
     *            a text string of unit to be removed
     * @return parsed result
     */
    public static Number removeUnit(final String str, final String unit) {
        final String sub = removeSuffix(str, unit);
        if (sub == null) {
            return null;
        }
        final StringTokenizer st = new StringTokenizer(sub);
        final ArrayList<String> tokenList = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            tokenList.add(st.nextToken());
        }
        if (tokenList.size() != 1) {
            return null;
        }
        final String value = tokenList.get(0);
        return SGUtilityText.getDouble(value);
    }

    /**
     * Remove the suffix from a text string.
     * 
     * @param str -
     *            a text string to be parsed
     * @param suffix -
     *            a text string to be removed
     * @return parsed result
     */
    public static String removeSuffix(final String str, final String suffix) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return null;
        }
        if (suffix == null) {
            return str;
        }

        if (str.endsWith(suffix) == false) {
            return str;
        }

        // create a new string without suffix
        int index = -1;
        while (true) {
            int num = str.indexOf(suffix, index + 1);
            if (num == -1) {
                break;
            }
            index = num;
        }
        String sub = str.substring(0, index);
        if (sub.length() == 0) {
            return null;
        }

        return sub;
    }

    /**
     * Default colors.
     */
    public static final Color[] DEFAULT_COLORS = { Color.RED, Color.GREEN,
            Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE,
            Color.PINK, Color.WHITE, Color.LIGHT_GRAY, Color.GRAY,
            Color.DARK_GRAY, Color.BLACK };

    /**
     * Names of default colors.
     */
    public static final String[] DEFAULT_COLOR_NAMES = { "RED", "GREEN",
            "BLUE", "CYAN", "MAGENTA", "YELLOW", "ORANGE", "PINK", "WHITE",
            "LIGHT_GRAY", "GRAY", "DARK_GRAY", "BLACK" };

    /**
     * Returns a color object of given name.
     * 
     * @param name
     *           the name of color
     * @return a color object
     */
    public static Color getColor(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name==null");
        }
        Color cl = null;
        for (int ii = 0; ii < DEFAULT_COLOR_NAMES.length; ii++) {
            if (SGUtilityText.isEqualString(DEFAULT_COLOR_NAMES[ii], name)) {
                cl = DEFAULT_COLORS[ii];
                break;
            }
        }
        return cl;
    }

    /**
     * Returns a color object with given component.
     * 
     * @param r
     *          the red component
     * @param g
     *          the green component
     * @param b
     *          the blue component
     * @return a color object
     */
    public static Color getColor(final String r, final String g, final String b) {
        Integer nr = getInteger(r);
        if (nr == null) {
            return null;
        }
        Integer ng = getInteger(g);
        if (ng == null) {
            return null;
        }
        Integer nb = getInteger(b);
        if (nb == null) {
            return null;
        }
        if (isValidColor(nr.intValue(), ng.intValue(), nb.intValue()) == false) {
            return null;
        }
        return new Color(nr.intValue(), ng.intValue(), nb.intValue());
    }

    /**
     * Checks whether a given color is valid.
     * 
     * @param r
     *          the red component
     * @param g
     *          the green component
     * @param b
     *          the blue component
     * @return true if a given color is valid
     */
    public static boolean isValidColor(final int r, final int g, final int b) {
        return isValidColorComponent(r) && isValidColorComponent(g) && isValidColorComponent(b);
    }

    private static boolean isValidColorComponent(final int c) {
        return (c >= 0 && c < 256);
    }
    
    /**
     * Returns a color object of
     * given name or
     * given text string in a format of "(r,g,b)" or "(r,g,b,a)".
     * 
     * @param str
     *           the name of color, "(r,g,b)" or "(r,g,b,a)"
     * @return a color object or null if failed
     */
    public static Color parseColorString(final String str) {
        Color color = getColor(str);
        if (null!=color) {
            return color;
        } else {
            return parseColor(str);
        }
    }
    
    /**
     * Returns the media size. If an argument is equal to null or an appropriate media size
     * is not found, this method returns null.
     * 
     * @param str
     *            a text string for the media size
     * @return the media size
     */
    public static final MediaSize getMediaSize(final String str) {
        if (str == null) {
            return null;
        }
        MediaSize size = null;
        if (SGUtilityText.isEqualString(PAPER_SIZE_A4, str)) {
            size = MediaSize.ISO.A4;
        } else if (SGUtilityText.isEqualString(PAPER_SIZE_A3, str)) {
            size = MediaSize.ISO.A3;
        } else if (SGUtilityText.isEqualString(PAPER_SIZE_B5, str)) {
            size = MediaSize.ISO.B5;
        } else if (SGUtilityText.isEqualString(PAPER_SIZE_B4, str)) {
            size = MediaSize.ISO.B4;
        } else if (SGUtilityText.isEqualString(PAPER_SIZE_US_LETTER, str)
                || SGUtilityText.isEqualString(PAPER_SIZE_US_LETTER_SIMPLE, str)) {
            size = MediaSize.NA.LETTER;
        }
        return size;
    }
    
    /**
     * Returns true if a given text string means "Portrait" and returns false if it
     * means "Landscape". Otherwise, returns null.
     * 
     * @param str
     *           a text string for paper direction
     * @return true if a given text string means the portrait direction
     */
    public static final Boolean isPortrait(final String str) {
        if (str == null) {
            return null;
        }
        if (SGUtilityText.isEqualString(PORTRAIT, str)) {
            return Boolean.TRUE;
        } else if (SGUtilityText.isEqualString(LANDSCAPE, str)) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    /**
     * Creates a dimension object from a given string in a format of "(w, h)".
     * 
     * @param str
     *           a text string
     * @return a dimension object if parsing succeeds
     */
    public static Dimension getDimension(final String str) {
        final int[] array = getIntegerArray(str);
        if (array == null) {
            return null;
        }

        final int len = array.length;
        if (len != 2) {
            return null;
        }
        if (array[0] < 0 || array[1] < 0) {
            return null;
        }

        final Dimension dim = new Dimension(array[0], array[1]);
        return dim;
    }

    /**
     * 
     * @param value
     * @return
     */
    public static Number getNumber(final String value, final StringBuffer u) {
        if (value == null) {
            return null;
        }
        String str = value.toLowerCase();

        String unit = null;
        String[] units = SGUtilityText.getUnitsArrayOfLength();
        for (int ii = 0; ii < units.length; ii++) {
            if (str.endsWith(units[ii])) {
                unit = units[ii];
                break;
            }
        }

        if (unit == null) {
            return null;
        }
        u.append(unit);

        Number num = SGUtilityText.removeUnit(str, unit);
        if (num == null) {
            return null;
        }

        return Double.valueOf(num.doubleValue());
    }

    // returns a string of "true" or "false"
    public static String getBooleanString(final String value) {
        final String sTrue = Boolean.TRUE.toString();
        final String sFalse = Boolean.FALSE.toString();
        Number num = SGUtilityText.getInteger(value);

        String ret = null;
        if (num != null) {
            ret = ("0".equals(value) ? sTrue : sFalse);
        } else {
            if (sTrue.equalsIgnoreCase(value)) {
                ret = sTrue;
            } else if (sFalse.equalsIgnoreCase(value)) {
                ret = sFalse;
            }
        }

        return ret;
    }

    /**
     * Returns a list of tokens of the first line.
     * 
     * @param br
     *           a buffered reader
     * @return a list of tokens
     */
    public static List<Token> getFirstTokenList(BufferedReader br) {
        List<Token> tokenList = new ArrayList<Token>();
        while (true) {
            String line = SGUtilityText.readLine(br);
            if (line == null) {
                break;
            }
            if (count(line, '"') % 2 != 0) {
                return null;
            }

            // tokenize the line
            tokenList.clear();
            SGCSVTokenizer csvt = new SGCSVTokenizer(line, true);
            while (csvt.hasMoreTokens()) {
                try {
                    Token result = csvt.nextToken();
                    tokenList.add(result);
                } catch (NoSuchElementException e) {
                }
            }
            final int size = tokenList.size(); // the number of tokens
            if (size != 0) {
                break;
            }
        }
        return tokenList;
    }

    /**
     * Counts and returns the number of appearance of a given character in a given text string.
     * 
     * @param str
     *           a text string
     * @param ch
     *           a character
     * @return the number of appearance of given character
     */
    public static int count(final String str, final char ch) {
        int cnt = 0;
        final int len = str.length();
        for (int ii = 0; ii < len; ii++) {
            final char c = str.charAt(ii);
            if (c == ch) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Returns whether a given text string is double quoted.
     * 
     * @param str
     *           a text string
     * @return true if a given text string is double quoted
     */
    public static boolean isDoubleQuoted(final String str) {
    	if (str.length() < 2) {
    		return false;
    	}
    	return (str.startsWith("\"") && str.endsWith("\""));
    }
    
    /**
     * Compares only letters and digits in given two text strings and returns whether
     * they are equal.
     * 
     * @param str1
     *           the first text string
     * @param str2
     *           the second text string
     * @return true if two text strings are equal
     */
    public static boolean isEqualString(final String str1, final String str2) {

    	if (str1 == null || str2 == null) {
    		throw new IllegalArgumentException("str1 == null || str2 == null");
    	}
    	
    	List<Character> cList1 = getCharList(str1);
    	List<Character> cList2 = getCharList(str2);
    	
    	return cList1.equals(cList2);
    }
    
    private static List<Character> getCharList(final String str) {
    	// to the upper case
    	final String ucStr = str.toUpperCase();
    	
    	// skip characters other than alphabets and numbers
    	List<Character> cList = new ArrayList<Character>();
    	for (int ii = 0; ii < ucStr.length(); ii++) {
    		final char c = ucStr.charAt(ii);
    		if (Character.isLetterOrDigit(c)) {
    			cList.add(Character.valueOf(c));
    		}
    	}
    	return cList;
    }
    
    private static char[] toArray(final List<Character> cList) {
    	char[] array = new char[cList.size()];
    	for (int ii = 0; ii < array.length; ii++) {
    		array[ii] = cList.get(ii).charValue();
    	}
    	return array;
    }
    
    /**
     * Compares only letters and digits in given two text strings and returns whether
     * a given string starts with given prefix.
     * 
     * @param str
     *           a text string
     * @param prefix
     *           the prefix
     * @return true if the text string starts with the prefix
     */
    public static boolean startsWith(final String str, final String prefix) {
    	
    	if (str == null || prefix == null) {
    		throw new IllegalArgumentException("str == null || prefix == null");
    	}
    	
    	List<Character> cList = getCharList(str);
    	List<Character> cListPrefix = getCharList(prefix);
    	char[] cArray = toArray(cList);
    	char[] cPrefixArray = toArray(cListPrefix);
    	String strNew = new String(cArray);
    	String prefixNew = new String(cPrefixArray);

    	return strNew.startsWith(prefixNew);
    }
    
    /**
     * Return only letters and digits string.
     * @param str
     *           a text string
     * @return only letters and digits string
     */
    public static String getCharString(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("str == null");
        }
        List<Character> cList = getCharList(str);
        char[] cArray = toArray(cList);
        return new String(cArray);
    }
    
    /**
     * This parses comma separated string that separated contents are key=value forms.
     * Return {key, value} string arrays.
     * 
     * ex.) "y=0,x=2" returns { { "y", "0" }, { "x", "2" } }
     * @param str
     * @return
     */
    public static String[][] readStringMaps(final String str) {
        String[] uncsv = str.split(",");
        String[][] result = new String[uncsv.length][2];
        for (int i = 0; i < uncsv.length; i++) {
            String[] unequal = uncsv[i].split("=");
            if (unequal.length>=2) {
                result[i][0] = unequal[0].trim();
                result[i][1] = unequal[1].trim();
            }
        }
        return result;
    }
    
    /**
     * Parses a text string and creates an array of integers.
     * 
     * @param str
     *           a text string
     * @return an array of integers or null if failed to parse
     */
    public static Integer[] parseIndices(final String str) {
        List<Integer> numList = new ArrayList<Integer>();
        if (str.length() != 0) {
            final int start = str.indexOf('{');
            final int end = str.lastIndexOf('}');
            if (start == -1 || end == -1) {
            	Integer num = SGUtilityText.getInteger(str);
            	if (num == null) {
            		return null;
            	}
            	return new Integer[] { num };
            }
            if (start > end) {
                return null;
            }
            String sub = str.substring(start + 1, end);
            StringTokenizer st = new StringTokenizer(sub, ",");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                Integer num = SGUtilityText.getInteger(token);
                if (num == null) {
                    return null;
                }
                numList.add(num);
            }
        }
        Integer[] indices = new Integer[numList.size()];
        for (int ii = 0; ii < indices.length; ii++) {
            indices[ii] = (Integer) numList.get(ii);
        }
        return indices;
    }
    
    /**
     * Parses a text string and creates an array of strings.
     * 
     * @param str
     *           a text string
     * @return an array of strings or null if failed to parse
     */
    public static String[] parseStrings(final String str) {
        String[] array = null;
        if (str.length() != 0) {
            final int start = str.indexOf('{');
            final int end = str.lastIndexOf('}');
            if (start == -1 || end == -1) {
                return new String[] { str };
            }
            if (start > end) {
                return null;
            }
            String sub = str.substring(start + 1, end);
            array = sub.split(",");
            for (int ii = 0; ii < array.length; ii++) {
                array[ii] = array[ii].trim();
            }
        }
        return array;
    }
    
    /**
     * Returns the axis direction ID from the name.
     * 
     * @param name
     *           the name of axis direction
     * @return the scale type ID
     */
    public static int getAxisDirection(final String name) {
        if (name == null) {
            return -1;
        }
        int type = -1;
        if (SGIFigureElementAxisConstants.AXIS_DIRECTION_NAME_HORIZONTAL.equalsIgnoreCase(name)) {
            type = SGIFigureElementAxisConstants.AXIS_DIRECTION_HORIZONTAL;
        } else if (SGIFigureElementAxisConstants.AXIS_DIRECTION_NAME_VERTICAL.equalsIgnoreCase(name)) {
            type = SGIFigureElementAxisConstants.AXIS_DIRECTION_VERTICAL;
        } else if (SGIFigureElementAxisConstants.AXIS_DIRECTION_NAME_COLOR_BAR.equalsIgnoreCase(name)) {
            type = SGIFigureElementAxisConstants.AXIS_DIRECTION_NORMAL;
        }
        return type;
    }

    /**
     * Returns a new string that is a substring of a given string searching backward starting at the index
     * of the specified character.
     * 
     * @param str
     *           a string
     * @param ch
     *           a character
     * @return a substring if it exists
     */
    public static final String lastSubstring(final String str, final char ch) {
    	final int index = str.lastIndexOf(ch);
    	if (index == -1) {
    		return null;
    	}
    	if (index == str.length() - 1) {
    		return null;
    	}
    	return str.substring(index + 1);
    }
    
    /**
     * Creates a map from a given text string in the format
     * key1:value1, key2:value2, ...
     * 
     * @param str
     *           a text string
     * @return created map
     */
    public static Map<String, String> createMap(String str) {
        String sub = SGUtilityText.getInnerString(str, '(', ')');
        if (sub == null) {
            return null;
        }
        List<String> tokenList = tokenizeCommand(sub);
        if (tokenList == null) {
        	return null;
        }
    	if (tokenList.size() == 0) {
    		return null;
    	}
    	Map<String, String> map = new HashMap<String, String>();
    	for (int ii = 0; ii < tokenList.size(); ii++) {
    		String token = tokenList.get(ii);
    		String[] tokens = token.trim().split(":");
    		if (tokens == null || tokens.length != 2) {
    			return null;
    		}
    		String name = tokens[0].trim();
    		String value = tokens[1].trim();
    		map.put(name, value);
    	}
    	return map;
    }
    
    /**
     * Creates a map that has integer keys from a given text string in the format
     * key1:value1, key2:value2, ...
     * 
     * @param str
     *           a text string
     * @return created map
     */
    public static Map<Integer, String> createIntegerKeyMap(String str) {
    	Map<String, String> map = createMap(str);
    	Map<Integer, String> iMap = new HashMap<Integer, String>();
    	Iterator<Entry<String, String>> itr = map.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, String> entry = itr.next();
    		String key = entry.getKey();
    		Integer num = SGUtilityText.getInteger(key);
    		if (num == null) {
    			return null;
    		}
    		String value = entry.getValue();
    		iMap.put(num, value);
    	}
    	return iMap;
    }

    /**
     * Tokenizes a text string for the command line input.
     * 
     * @param args
     *            a text string for the command line input
     * @return the list of tokens
     */
    public static List<String> tokenizeCommand(final String args) {
        final int len = args.length();
    	
        // find commas
        List<Integer> cIndexList = new ArrayList<Integer>();
        int cntDq = 0;
        int cntBracket = 0;
        for (int ii = 0; ii < len; ii++) {
        	final char c = args.charAt(ii);
        	if (c == '"') {
        		boolean escaped = false;
        		if (cntDq % 2 != 0) {
        			// inside a string
            		if (ii >= 1) {
            			final char cPrev = args.charAt(ii - 1);
            			if (cPrev == '\\') {
            				escaped = true;
            			}
            		}
        		}
        		if (!escaped) {
            		cntDq++;
        		}
        	} else if (c == '(') {
                if (cntDq % 2 == 0) {
                    // outside a string
                    cntBracket++;
                }
            } else if (c == ')') {
                if (cntDq % 2 == 0) {
                    // outside a string
                    cntBracket--;
                }
            } else if (c == ',') {
                if (cntDq % 2 == 0 && cntBracket == 0) {
                    // outside a string and a bracket
                    cIndexList.add(Integer.valueOf(ii));
                }
            }
        }
        if (cntDq % 2 != 0) {
        	return null;
        }
        if (cntBracket != 0) {
        	return null;
        }

        // split the argument to tokens
        int curIndex = 0;
        List<String> tokenList = new ArrayList<String>();
        for (int ii = 0; ii < cIndexList.size(); ii++) {
        	final int index = cIndexList.get(ii);
        	String token = args.substring(curIndex, index);
            token = token.trim();
        	tokenList.add(token);
        	curIndex = index + 1;
        	if (curIndex >= len) {
        		tokenList.add("");
        		break;
        	}
        }
		tokenList.add(args.substring(curIndex, len).trim());
		
		/*
    	final List<String> argsList = new ArrayList<String>();

		// reduce successive commas in each token
		for (int ii = 0; ii < tokenList.size(); ii++) {
			String token = tokenList.get(ii);
			final int fIndex = token.indexOf('"');
			if (fIndex == -1) {
				argsList.add(token);
				continue;
			}
			final int lIndex = token.lastIndexOf('"');
			if (lIndex == fIndex) {
				return null;
			}
            if (lIndex != token.length() - 1) {
                return null;
            }
			String head = token.substring(0, fIndex);
			String body = token.substring(fIndex + 1, lIndex);

            // check whether double quotations are successive
            for (int jj = 0; jj < body.length(); jj++) {
                final char c = body.charAt(jj);
                if (c == '"') {
                    if (jj == body.length() - 1) {
                        return null;
                    }
                    final char cNext = body.charAt(jj + 1);
                    if (cNext != '"') {
                        return null;
                    }
                    jj++;
                }
            }
            
			List<Character> cList = new ArrayList<Character>();
			boolean dq = false;
			for (int jj = 0; jj < body.length(); jj++) {
				final char c = body.charAt(jj);
				if (c == '"') {
                    if (!dq) {
                        dq = true;
                    } else {
                        cList.add(Character.valueOf(c));
                        dq = false;
                    }
				} else {
                    cList.add(Character.valueOf(c));
				}
			}
			char[] cArray = new char[cList.size()];
			for (int jj = 0; jj < cArray.length; jj++) {
				cArray[jj] = cList.get(jj).charValue();
			}
			StringBuffer sb = new StringBuffer();
			sb.append(head);
			sb.append('"');
			sb.append(new String(cArray));
			sb.append('"');
			argsList.add(sb.toString());
		}

		return argsList;
		*/
		
		return tokenList;
    }
    
    public static Color parseColorText(final String value) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
        	return cl;
        }
    	cl = SGUtilityText.parseColor(value);
        if (cl != null) {
        	return cl;
        }
        return null;
    }

    public static Color parseColorIncludingList(String str) {
    	Color innerColor = null;
        List<Color> colorList = SGUtilityText.getColorList(str);
        if (colorList != null) {
            if (colorList.size() == 0) {
                return null;
            }
            innerColor = colorList.get(0);
        } else {
        	innerColor = SGUtilityText.parseColor(str);
        }
        return innerColor;
    }
    
    /**
     * Detects the character set of a text file of given path.
     * 
     * @param path
     *           the file path
     * @return 
     */
	public static String detectCharacterSet(String path) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			return null;
		}
		UniversalDetector detector = new UniversalDetector(null);
		byte[] buf = new byte[4096];
		int nread = -1;
		try {
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
		} catch (IOException e) {
			return null;
		}
		detector.dataEnd();
		try {
			fis.close();
		} catch (IOException e) {
		}
		return detector.getDetectedCharset();
	}
	
	public static Period getPeriod(final String str) {
		Period p = null;
		try {
	    	p = Period.parse(str);
		} catch (IllegalArgumentException e) {
		}
    	return p;
	}
	
	public static SGDate getDate(String str) {
		SGDate date = null;
		try {
			date = new SGDate(str);
		} catch (ParseException e) {
		}
		return date;
	}
}
