package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface that declares static constants for text data.
 * 
 * @author kuromaru
 *
 */
public interface SGITextDataConstants {

    public static final String DOUBLE_QUATE = "\""; //$NON-NLS-1$

    public static final String WHITE_SPACE = " \t\n\r\f"; //$NON-NLS-1$

    public static final String SEPARATOR_COMMA = ","; //$NON-NLS-1$

    public static final int SEPARATOR_LEN = 1;

    public static final int DOUBLE_QUATE_LEN = 1;

    /**
     * A text string that denotes a value "Not a number".
     */
    public static final String NOT_A_NUMBER_VALUE = "NaN";
    
    /**
     * Prefix for comment lines.
     */
    public static final String DATA_COMMENT_PREFIX = "#";
    
    /**
     * The prefix for the header of a comment line.
     */
    public static final String DATA_COMMENT_HEADER_PREFIX = "<";

    /**
     * The suffix for the header of a comment line.
     */
    public static final String DATA_COMMENT_HEADER_SUFFIX = ">";
    
    /**
     * A header for a line that is "not" a comment line.
     */
    public static final String HEADER_NOT_A_COMMENT_LINE = "NAC";
    
}
