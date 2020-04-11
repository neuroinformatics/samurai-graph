/* ------------------------------
 * CSVTokenizer.java
 * ------------------------------
 * (C)opyright 2003, abupon (Manabu Hashimoto)
 * This class is based on the CSV tokenizer found at
 * http://sourceforge.net/projects/csvtokenizer/
 */

package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The csv tokenizer class allows an application to break a Comma Separated
 * Value format into tokens. The tokenization method is much simpler than the
 * one used by the <code>StringTokenizer</code> class. The
 * <code>CSVTokenizer</code> methods do not distinguish among identifiers,
 * numbers, and quoted strings, nor do they recognize and skip comments.
 * <p>
 * The set of separator (the characters that separate tokens) may be specified
 * either at creation time or on a per-token basis.
 * <p>
 * A <tt>CSVTokenizer</tt> object internally maintains a current position
 * within the string to be tokenized. Some operations advance this current
 * position past the characters processed.
 * <p>
 * A token is returned by taking a substring of the string that was used to
 * create the <tt>CSVTokenizer</tt> object.
 * <p>
 * The following is one example of the use of the tokenizer. The code:
 * <blockquote>
 * 
 * <pre>
 * CSVTokenizer csvt = new CSVTokenizer(&quot;this,is,a,test&quot;);
 * while (csvt.hasMoreTokens()) {
 *     println(csvt.nextToken());
 * }
 * </pre>
 * 
 * </blockquote>
 * <p>
 * prints the following output: <blockquote>
 * 
 * <pre>
 *   
 *        this
 *        is
 *        a
 *        test
 *    
 * </pre>
 * 
 * </blockquote>
 * 
 * @author abupon
 * @version
 * @see
 * @since
 */
public class SGCSVTokenizer implements Enumeration, SGITextDataConstants {

    private String record;

    private int currentIndex;

    private boolean is_csv_mode = false;

    private boolean is_comment_line = false;

    /**
     * A list of characters of white space.
     */
    private static List<Character> mWhiteSpaceList = new ArrayList<Character>();
    
    /**
     * An array of text strings for a header of a comment line.
     */
    private static final String[] COMMENT_HEADER_CONSTANTS = { HEADER_NOT_A_COMMENT_LINE };

    /**
     * An array of comment headers.
     */
    private static final String[] mCommentHeaders;
    
    static {
        // add to a list of white space characters
        for (int ii = 0; ii < WHITE_SPACE.length(); ii++) {
            mWhiteSpaceList.add(Character.valueOf(WHITE_SPACE.charAt(ii)));
        }
        
        // create regular expression patterns
        mCommentHeaders = new String[COMMENT_HEADER_CONSTANTS.length];
        for (int ii = 0; ii < COMMENT_HEADER_CONSTANTS.length; ii++) {
            StringBuffer sb = new StringBuffer();
            sb.append(DATA_COMMENT_HEADER_PREFIX);
            sb.append(COMMENT_HEADER_CONSTANTS[ii]);
            sb.append(DATA_COMMENT_HEADER_SUFFIX);
            mCommentHeaders[ii] = sb.toString();
        }
    }

    /**
     * Constructs a csv tokenizer for the specified string.
     * <code>theSeparator</code> argument is the separator for separating
     * tokens.
     * 
     * @param aString
     *            a string to be parsed.
     * @param isDataFile
     *            a data file reading flag
     */
    public SGCSVTokenizer(final String aString, final boolean isDataFile) {
        this.record = aString.trim();
        this.currentIndex = 0;
        // check comment line
        if (isDataFile && this.record.startsWith(DATA_COMMENT_PREFIX)) {//$NON-NLS-1$
            this.is_comment_line = true;
            for (int ii = 0; ii < mCommentHeaders.length; ii++) {
                this.record = this.record.substring(DATA_COMMENT_PREFIX.length());
                final int headLen = mCommentHeaders[ii].length();
                if (this.record.startsWith(mCommentHeaders[ii]) && this.record.length() != headLen) {
                    this.is_comment_line = false;
                    this.record = this.record.substring(headLen);
                    break;
                }
            }
        }
        // check comma separated mode
        char c;
        boolean in_quote = false;
        for (int ii = 0; ii < this.record.length(); ii++) {
            c = this.record.charAt(ii);
            if (in_quote) {
                if (c == '"') {
                    in_quote = false;
                }
            } else {
                if (c == '"') {
                    in_quote = true;
                } else if (c == ',') {
                    this.is_csv_mode = true;
                }
            }
        }
    }

    /**
     * Tests if there are more tokens available from this tokenizer's string. If
     * this method returns <tt>true</tt>, then a subsequent call to
     * <tt>nextToken</tt> with no argument will successfully return a token.
     * 
     * @return <code>true</code> if and only if there is at least one token in
     *         the string after the current position; <code>false</code>
     *         otherwise.
     */
    public boolean hasMoreTokens() {
        if (this.is_comment_line) {
            return false;
        }
        return (this.currentIndex >= 0);
    }

    /**
     * Returns the next token from this string tokenizer.
     * 
     * @return the next token from this string tokenizer.
     * @exception NoSuchElementException
     *                if there are no more tokens in this tokenizer's string.
     * @exception IllegalArgumentException
     *                if given parameter string format was wrong
     */
    public Token nextToken() throws NoSuchElementException,
            IllegalArgumentException {
    	StringBuffer sb = new StringBuffer();
        String token = null;
        int start;
        int end;
        boolean isText = false;
        if (!this.hasMoreTokens()) {
            throw new NoSuchElementException();
        }
        if (this.record.startsWith(SGCSVTokenizer.DOUBLE_QUATE,
                this.currentIndex)) {
            String rec = this.record.substring(this.currentIndex
                    + SGCSVTokenizer.DOUBLE_QUATE_LEN);
//            token = ""; //$NON-NLS-1$
            isText = true;
            for (;;) {
                end = rec.indexOf(SGCSVTokenizer.DOUBLE_QUATE);
                if (end < 0) {
                    throw new IllegalArgumentException("Illegal format"); //$NON-NLS-1$
                }
                if (!rec.startsWith(SGCSVTokenizer.DOUBLE_QUATE, end + 1)) {
                	sb.append(rec.substring(0, end));
//                    token = token + rec.substring(0, end);
                    break;
                }
                sb.append(rec.substring(0, end + 1));
//                token = token + rec.substring(0, end + 1);
                rec = rec.substring(end + SGCSVTokenizer.DOUBLE_QUATE_LEN * 2);
                this.currentIndex++;
            }
            // don't trim string
//            this.currentIndex += token.length()
//                    + SGCSVTokenizer.DOUBLE_QUATE_LEN * 2;
			this.currentIndex += sb.length() + SGCSVTokenizer.DOUBLE_QUATE_LEN * 2;
            if (this.is_csv_mode) {
                this.currentIndex = nextTokenIndexOf(this.currentIndex);
            }
            this.currentIndex += SGCSVTokenizer.SEPARATOR_LEN;
            this.currentIndex = nextTokenIndexOf(this.currentIndex);
            // this.currentIndex += (token.length()
            // + SGCSVTokenizer.DOUBLE_QUATE_LEN * 2 +
            // SGCSVTokenizer.SEPARATOR_LEN);
            // this.currentIndex = nextTokenIndexOf(this.currentIndex);
            // if (!this.is_csv_mode) {
            // this.currentIndex = nextTokenIndexOf(this.currentIndex);
            // }
            if (this.currentIndex >= this.record.length()) {
                this.currentIndex = -1;
            }
            token = sb.toString();
        } else {
            start = this.currentIndex;
            if (this.is_csv_mode) {
                end = this.record.indexOf(SEPARATOR_COMMA, this.currentIndex);
            } else {
                end = nextSeparatorIndexOf(this.currentIndex);
            }
            if (end >= 0) {
//                token = this.record.substring(start, end);
                sb.append(this.record.substring(start, end));
                // if (this.is_csv_mode) {
                // this.currentIndex = end + SEPARATOR_LEN;
                // } else {
                // this.currentIndex = nextTokenIndexOf(end);
                // if (this.currentIndex == this.record.length())
                // this.currentIndex = -1;
                // }

                final int offset = (this.is_csv_mode) ? SEPARATOR_LEN : 0;
                this.currentIndex = nextTokenIndexOf(end + offset);
                if (this.currentIndex == this.record.length()) {
                    boolean stop = true;
                    if (this.is_csv_mode) {
                        // to take into account commas at the end of the line
                        if (this.record.charAt(this.currentIndex - 1) == ',') {
                            stop = false;
                        }
                    }
                    if (stop) {
                        this.currentIndex = -1;
                    }
                }
                // if (this.currentIndex == this.record.length()) {
                // this.currentIndex = -1;
                // }

            } else {
                // end of line reached
                if (this.currentIndex == this.record.length()) {
//                    token = ""; //$NON-NLS-1$
                } else {
//                    token = this.record.substring(start);
                	sb.append(this.record.substring(start));
                }
                this.currentIndex = -1;
            }
            token = sb.toString();
            token = token.trim();
        }
        // return token;
        return new Token(token, isText);
    }

    private int nextSeparatorIndexOf(int fromIndex) {
        char c;
        int cnt = 0;
        int ii;
        int len = this.record.length();
        if (len == fromIndex) {
            return -1;
        }
        for (ii = fromIndex; ii < len; ii++) {
            c = this.record.charAt(ii);
            if (mWhiteSpaceList.contains(Character.valueOf(c))) {
                break;
            }
            cnt++;
        }
        if (ii == len) {
            return -1;
        }
        return cnt + fromIndex;
    }

    private int nextTokenIndexOf(int fromIndex) {
        char c;
        int cnt = 0;
        int len = this.record.length();
        for (int ii = fromIndex; ii < len; ii++) {
            c = this.record.charAt(ii);
            if (!mWhiteSpaceList.contains(Character.valueOf(c))) {
                break;
            }
            cnt++;
        }
        return cnt + fromIndex;
    }

    /**
     * Returns the same value as the <code>hasMoreTokens</code> method. It
     * exists so that this class can implement the <code>Enumeration</code>
     * interface.
     * 
     * @return <code>true</code> if there are more tokens; <code>false</code>
     *         otherwise.
     * @see java.util.Enumeration
     * @see java.util.SGCSVTokenizer#hasMoreTokens()
     */
    public boolean hasMoreElements() {
        return hasMoreTokens();
    }

    /**
     * Returns the same value as the <code>nextToken</code> method, except
     * that its declared return value is <code>Object</code> rather than
     * <code>String</code>. It exists so that this class can implement the
     * <code>Enumeration</code> interface.
     * 
     * @return the next token in the string.
     * @exception NoSuchElementException
     *                if there are no more tokens in this tokenizer's string.
     * @see java.util.Enumeration
     * @see java.util.SGCSVTokenizer#nextToken()
     */
    public Object nextElement() {
        return nextToken();
    }

    /**
     * A token class.
     * 
     * @author kuromaru
     * 
     */
    public static class Token {

        // a text string
        private String string;

        // a flag whether this token is double-quoted
        private boolean doubleQuoted;

        /**
         * Builds a token.
         * 
         * @param string
         *            a text string
         * @param dq
         *            whether this token is double-quoted
         */
        public Token(String string, boolean dq) {
            this.string = string;
            this.doubleQuoted = dq;
        }

        /**
         * Returns a text string of this token.
         */
        public String toString() {
            return this.getString();
        }

        /**
         * Returns a text string of this token.
         * 
         * @return a text string of this token
         */
        public String getString() {
            return this.string;
        }

        /**
         * Returns whether this token is double-quoted.
         * 
         * @return true when this token is double-quoted
         */
        public boolean isDoubleQuoted() {
            return this.doubleQuoted;
        }
    }

    // public static void main(String[] args) {
    // int i = 1;
    // String str;
    // String expect;
    // String result;

    // str = "1, \t 2, \"\" 3, 4, \"a, \"\"\\hoge\"";
    // str = "1, \t 2, \"\" 3, b 4, a hoge";
    // System.out.println("String : [" + str + "]");
    // SGCSVTokenizer csvt = new SGCSVTokenizer(str, true);
    // i = 1;
    // while (csvt.hasMoreTokens()) {
    // try {
    // expect = String.valueOf(i++);
    // result = csvt.nextToken();
    // System.out.print(expect + ": [");
    // System.out.println(result + "]");
    // } catch (NoSuchElementException e) {
    // e.printStackTrace();
    // System.exit(-1);
    // }
    // }

    // }

    public static void main(String[] args) {

        // String str = ",,";
        // String str = " , , ";
        // String str = ",a, \t ";
        // String str = " , a , \t ";

        // String str = "x,y,,z";
        // String str = "x,y,,z,";
        // String str = "x, y, , z, ";

        // space
        // String str = "aaa bbb ccc";
        // String str = "\"aa a\" \"bb\"\"b\" \"cc,c\"";
        // String str = "\" aa a \" \" bb\"\"b \" \" cc,c \"";
        // String str = "\"\"\"aa a\"\"\" \"\"\"bb\"\"b\"\"\" \"\"\"cc,c\"\"\"";
        // String str = "\" \"\"aa a\"\" \" \" \"\"bb\"\"b\"\" \" \"  \"\"cc,c\"\" \"";
        // String str = "\"\"\" aa a \"\"\" \"\"\" bb\"\"b \"\"\" \"\"\" cc,c\"\"\"";

        // comma
        // String str = "aaa,bbb,ccc";
        // String str = "\"aa a\",\"bb\"\"b\",\"cc,c\"";
        // String str = "\" aa a \",\" bb\"\"b \",\" cc,c \"";
        // String str = "\"\"\"aa a\"\"\",\"\"\"bb\"\"b\"\"\",\"\"\"cc,c\"\"\"";
        // String str = "\" \"\"aa a\"\" \",\" \"\"bb\"\"b\"\" \",\" \"\"cc,c\"\" \"";
        // String str = "\" \"\" aa a\"\" \",\" \"\"bb\"\"b \"\" \",\" \"\"c c ,c\"\" \"";
        // String str = "\" \"\" a ,a a\"\" \",\" \"\"bb \"\",b \"\" \",\" \"\"c \"\"c, c \"\" \"";

        // comma + space
        // String str = " aaa, bbb, ccc ";
        // String str = " \"aa a\", \"bb\"\"b\", \"cc,c\" ";
        // String str = " \" aa a \", \" bb\"\"b \", \" cc,c \" ";
        // String str = " \"\"\"aa a\"\"\", \"\"\"bb\"\"b\"\"\", \"\"\"cc,c\"\"\" ";
        // String str = " \" \"\"  aa a\"\" \", \" \"\"bb\"\"b  \"\" \", \" \"\"c c ,c\"\" \"";

        // space + comma + space
        // String str = "\"aaa\", bbb";
        // String str = "\"aaa\" , bbb";
        // String str = " \" \"\"aa a\"\" \"  ,  \" \"\"bb\"\"b\"\" \" ,  \" \"\"cc,c\"\" \"";
        String str = "   \"  \"\"  aa a\"\" \"  ,  \" \"\"bb\"\"b  \"\"  \" ,  \" \"\" c c ,c\"\" \"  ";
        
        System.out.println("#" + str + "#");

        SGCSVTokenizer csvt = new SGCSVTokenizer(str, true);
        while (csvt.hasMoreTokens()) {
            Token token = csvt.nextToken();
            System.out.println("*" + token.getString() + "*");
        }
    }

}
