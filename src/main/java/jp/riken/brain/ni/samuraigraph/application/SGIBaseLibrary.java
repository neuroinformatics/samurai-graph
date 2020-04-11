package jp.riken.brain.ni.samuraigraph.application;
import com.sun.jna.Library;
import com.sun.jna.Platform;

/**
 * The base interface for Java Native Access.
 *
 */
public interface SGIBaseLibrary extends Library {
	
	/**
	 * The constant for true.
	 */
	public static final int TRUE = 1;

	/**
	 * The constant for false.
	 */
	public static final int FALSE = 0;
	
	/**
	 * The number of bytes of an integer variable.
	 */
	public static final int SIZE_OF_INT = 4;
	
	/**
	 * The number of bytes of a double variable.
	 */
	public static final int SIZE_OF_DOUBLE = 8;

	/**
	 * The number of bytes of a pointer variable.
	 */
	public static final int SIZE_OF_POINTER = Platform.is64Bit() ? 8 : 4;
}
