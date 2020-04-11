package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMultipleDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZGridDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYGridDataBuffer;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * An interface for the library with Java Native Access.
 *
 */
public interface SGIDataCalcLibrary extends SGIBaseLibrary, SGIDataPluginConstants {
	
	/**
	 * Processes given data and returns the result.
	 * 
	 * @param input
	 *           pointer for input data
	 * @return pointer for the result of calculation
	 */
	public Pointer calc(Pointer input);

	/**
	 * Frees the memory of given data buffer.
	 * 
	 * @param buffer
	 *           data buffer
	 */
	public void freeData(Pointer buffer);

	/**
	 * The base class of the structure.
	 *
	 */
	public static abstract class AbstractStructure extends Structure {
		/**
		 * The default constructor.
		 */
		public AbstractStructure() {
			super();
		}
	}

	/**
	 * The base class of the structure of an array type object.
	 *
	 */
	public static abstract class ArrayStructure extends AbstractStructure {
		/**
		 * The default constructor.
		 */
		public ArrayStructure() {
			super();
			this.initLength();
		}

		/**
		 * Builds an object with a given pointer.
		 * 
		 * @param p
		 *           a pointer
		 */
		public ArrayStructure(Pointer p) {
			super();
			this.initLength();
			this.useMemory(p);
			this.read();
		}
		
		/**
		 * Initializes the length.
		 */
		protected abstract void initLength();
	}

	/**
	 * Structure for an array of integer numbers.
	 *
	 */
	public static class IntegerArrayStructure extends ArrayStructure {
		public static int length = 0;
		public int[] values;
		public IntegerArrayStructure() {
			super();
		}
		public IntegerArrayStructure(Pointer p) {
			super(p);
		}
		protected void initLength() {
			this.values = new int[length];
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "values");
		}
	}

	/**
	 * Structure for an array of double numbers.
	 *
	 */
	public static class DoubleArrayStructure extends ArrayStructure {
		public static int length = 0;
		public double[] values;
		public DoubleArrayStructure() {
			super();
		}
		public DoubleArrayStructure(Pointer p) {
			super(p);
		}
		protected void initLength() {
			this.values = new double[length];
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "values");
		}
	}

	/**
	 * Structure for a pointer object.
	 *
	 */
	public static class PointerStructure extends AbstractStructure {
		public Pointer pointer;
		public PointerStructure() {
			super();
		}
		public PointerStructure(Pointer p) {
			super();
			this.useMemory(p);
			this.read();
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("pointer");
		}
	}
	
	/**
	 * The base class for the structure of an one-dimensional array.
	 *
	 */
	public static abstract class OneDimensionalDataStructure extends ArrayStructure {
		public static int length = 0;
		public int len;
		public OneDimensionalDataStructure() {
			super();
		}
		public OneDimensionalDataStructure(Pointer p) {
			super(p);
		}
		protected void initLength() {
			this.len = length;
		}
	}

	/**
	 * The base class for the structure of a two-dimensional array.
	 *
	 */
	public static abstract class TwoDimensionalDoubleStructure extends ArrayStructure {
		public static int xLength = 0;
		public static int yLength = 0;
		public int xLen;
		public int yLen;
		public TwoDimensionalDoubleStructure() {
			super();
		}
		public TwoDimensionalDoubleStructure(Pointer p) {
			super(p);
		}
		protected void initLength() {
			this.xLen = xLength;
			this.yLen = yLength;
		}
	}

	/**
	 * Structure for a scalar-XY type data.
	 *
	 */
	public static class SXYDataStructure extends OneDimensionalDataStructure {
		public int num;
		public Pointer xValuesPointer;
		public Pointer yValuesPointer;
		public SXYDataStructure() {
			super();
		}
		public SXYDataStructure(Pointer p) {
			super(p);
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "len", "num", "xValuesPointer", "yValuesPointer");
		}
	}

	/**
	 * Structure for an one-dimensional scalar-XYZ type data.
	 *
	 */
	public static class SXYZDataStructure extends OneDimensionalDataStructure {
		public Pointer xValuesPointer;
		public Pointer yValuesPointer;
		public Pointer zValuesPointer;
		public SXYZDataStructure() {
			super();
		}
		public SXYZDataStructure(Pointer p) {
			super(p);
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "len", "xValuesPointer", "yValuesPointer", "zValuesPointer");
		}
	}

	/**
	 * Structure for an one-dimensional vector-XY type data.
	 *
	 */
	public static class VXYDataStructure extends OneDimensionalDataStructure {
		public Pointer xValuesPointer;
		public Pointer yValuesPointer;
		public Pointer fValuesPointer;
		public Pointer sValuesPointer;
		public int polarFlag;
		public VXYDataStructure() {
			super();
		}
		public VXYDataStructure(Pointer p) {
			super(p);
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "len", "xValuesPointer", "yValuesPointer", "fValuesPointer", "sValuesPointer", "polarFlag");
		}
	}
	
	/**
	 * Structure for an two-dimensional scalar-XYZ type data.
	 *
	 */
	public static class SXYZGridDataStructure extends TwoDimensionalDoubleStructure {
		public Pointer xValuesPointer;
		public Pointer yValuesPointer;
		public Pointer zValuesPointer;
		public SXYZGridDataStructure() {
			super();
		}
		public SXYZGridDataStructure(Pointer p) {
			super(p);
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("xLength", "yLength", "xLen", "yLen", "xValuesPointer", "yValuesPointer", "zValuesPointer");
		}
	}

	/**
	 * Structure for an two-dimensional vector-XY type data.
	 *
	 */
	public static class VXYGridDataStructure extends TwoDimensionalDoubleStructure {
		public Pointer xValuesPointer;
		public Pointer yValuesPointer;
		public Pointer fValuesPointer;
		public Pointer sValuesPointer;
		public int polarFlag;
		public VXYGridDataStructure() {
			super();
		}
		public VXYGridDataStructure(Pointer p) {
			super(p);
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("xLength", "yLength", "xLen", "yLen", "xValuesPointer", "yValuesPointer", "fValuesPointer", "sValuesPointer", "polarFlag");
		}
	}

	/**
	 * Structure for a text string.
	 *
	 */
	public static class StringStructure extends AbstractStructure {
		public String str;
		public StringStructure() {
			super();
		}
		public StringStructure(Pointer p) {
			super();
			this.useMemory(p);
			this.read();
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("str");
		}
	}

	/**
	 * Structure for data array.
	 *
	 */
	public static class DataArrayStructure extends ArrayStructure {
		public static int dataNum = 0;
		public int num;
		public Pointer dataTypeArrayPointer;
		public Pointer dataArrayPointer;
		public DataArrayStructure() {
			super();
		}
		public DataArrayStructure(Pointer p) {
			super(p);
		}
		protected void initLength() {
			this.num = dataNum;
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("dataNum", "num", "dataTypeArrayPointer", "dataArrayPointer");
		}
	}

	/**
	 * Structure for input data.
	 *
	 */
	public static class InputDataStructure extends AbstractStructure {
		public Pointer dataBufferPointer;
		public int parametersNum;
		public Pointer parametersPointer;
		public InputDataStructure() {
			super();
		}
		public InputDataStructure(Pointer p) {
			super();
			this.useMemory(p);
			this.read();
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("dataBufferPointer", "parametersNum", "parametersPointer");
		}
	}

	/**
	 * Structure for output data.
	 *
	 */
	public static class OutputDataStructure extends AbstractStructure {
		public Pointer dataBufferPointer;
		public int errmsgNum;
		public Pointer errmsgPointer;
		public OutputDataStructure() {
			super();
		}
		public OutputDataStructure(Pointer p) {
			super();
			this.useMemory(p);
			this.read();
		}
		protected List<String> getFieldOrder() {
			return Arrays.asList("dataBufferPointer", "errmsgNum", "errmsgPointer");
		}
	}

	public static class Writer {
		
		public static Pointer toPointer(int[] values) {
			if (values.length == 0) {
				return null;
			}
			IntegerArrayStructure.length = values.length;
			IntegerArrayStructure st = new IntegerArrayStructure();
			st.values = values.clone();
			st.write();
			return st.getPointer();
		}
		
		public static Pointer toPointer(double[] values) {
			if (values.length == 0) {
				return null;
			}
			DoubleArrayStructure.length = values.length;
			DoubleArrayStructure st = new DoubleArrayStructure();
			st.values = values.clone();
			st.write();
			return st.getPointer();
		}

		public static Pointer toPointer(double[][] values) {
			if (values.length == 0) {
				return null;
			}
			Pointer[] pointers = new Pointer[values.length];
			for (int ii = 0; ii < pointers.length; ii++) {
				pointers[ii] = toPointer(values[ii]);
			}
			return toPointer(pointers);
		}
		
		public static Pointer toPointer(String[] strArray) {
			if (strArray.length == 0) {
				return null;
			}
			StringStructure headBuffer = new StringStructure();
			Structure[] structArray = headBuffer.toArray(strArray.length);
			for (int ii = 0; ii < structArray.length; ii++) {
				Pointer p = structArray[ii].getPointer();
				StringStructure st = new StringStructure(p);
				st.str = strArray[ii];
				st.write();
			}
			return headBuffer.getPointer();
		}

		public static Pointer toPointer(Pointer[] pointers) {
			if (pointers.length == 0) {
				return null;
			}
			PointerStructure headBuffer = new PointerStructure();
			Structure[] structArray = headBuffer.toArray(pointers.length);
			for (int ii = 0; ii < structArray.length; ii++) {
				Pointer p = structArray[ii].getPointer();
				PointerStructure st = new PointerStructure(p);
				st.pointer = pointers[ii];
				st.write();
			}
			return headBuffer.getPointer();
		}
		
		public static void write(SGSXYMultipleDataBuffer buffer, Pointer p) {
			SXYDataStructure.length = buffer.getLength();
			SXYDataStructure st = new SXYDataStructure(p);
			SGSXYDataBuffer[] sxyBuffers = buffer.getSXYDataBufferArray();
			Pointer[] xPointers = new Pointer[sxyBuffers.length];
			Pointer[] yPointers = new Pointer[sxyBuffers.length];
			for (int ii = 0; ii < sxyBuffers.length; ii++) {
				double[] xValues = sxyBuffers[ii].getXValues();
				double[] yValues = sxyBuffers[ii].getYValues();
				xPointers[ii] = toPointer(xValues);
				yPointers[ii] = toPointer(yValues);
			}
			st.len = buffer.getLength();
			st.num = buffer.getMultiplicity();
			st.xValuesPointer = toPointer(xPointers);
			st.yValuesPointer = toPointer(yPointers);
			st.write();
		}

		public static void write(SGSXYZDataBuffer buffer, Pointer p) {
			SXYZDataStructure.length = buffer.getLength();
			SXYZDataStructure st = new SXYZDataStructure(p);
			st.len = buffer.getLength();
			st.xValuesPointer = toPointer(buffer.getXValues());
			st.yValuesPointer = toPointer(buffer.getYValues());
			st.zValuesPointer = toPointer(buffer.getZValues());
			st.write();
		}

		public static void write(SGVXYDataBuffer buffer, Pointer p) {
			VXYDataStructure.length = buffer.getLength();
			VXYDataStructure st = new VXYDataStructure(p);
			st.len = buffer.getLength();
			st.xValuesPointer = toPointer(buffer.getXValues());
			st.yValuesPointer = toPointer(buffer.getYValues());
			st.fValuesPointer = toPointer(buffer.getFirstComponentValues());
			st.sValuesPointer = toPointer(buffer.getSecondComponentValues());
			st.polarFlag = toBooleanValue(buffer.isPolar());
			st.write();
		}

		public static void write(SGSXYZGridDataBuffer buffer, Pointer p) {
			SXYZGridDataStructure.xLength = buffer.getLengthX();
			SXYZGridDataStructure.yLength = buffer.getLengthY();
			SXYZGridDataStructure st = new SXYZGridDataStructure(p);
			st.xLen = buffer.getLengthX();
			st.yLen = buffer.getLengthY();
			st.xValuesPointer = toPointer(buffer.getXValues());
			st.yValuesPointer = toPointer(buffer.getYValues());
			st.zValuesPointer = toPointer(buffer.getZValues());
			st.write();
		}

		public static void write(SGVXYGridDataBuffer buffer, Pointer p) {
			VXYGridDataStructure.xLength = buffer.getLengthX();
			VXYGridDataStructure.yLength = buffer.getLengthY();
			VXYGridDataStructure st = new VXYGridDataStructure(p);
			st.xLen = buffer.getLengthX();
			st.yLen = buffer.getLengthY();
			st.xValuesPointer = toPointer(buffer.getXValues());
			st.yValuesPointer = toPointer(buffer.getYValues());
			st.fValuesPointer = toPointer(buffer.getFirstComponentValues());
			st.sValuesPointer = toPointer(buffer.getSecondComponentValues());
			st.polarFlag = toBooleanValue(buffer.isPolar());
			st.write();
		}

		public static void write(SGDataBuffer[] buffers, Pointer p) {
			DataArrayStructure.dataNum = buffers.length;
			DataArrayStructure st = new DataArrayStructure(p);
			
			st.num = buffers.length;
			
			int[] dataTypes = new int[buffers.length];
			for (int ii = 0; ii < dataTypes.length; ii++) {
				SGDataBuffer buffer = buffers[ii];
				int type = -1;
				if (buffer instanceof SGSXYDataBuffer
						|| buffer instanceof SGSXYMultipleDataBuffer) {
					type = DATA_TYPE_SXY;
				} else if (buffer instanceof SGSXYZDataBuffer) {
					type = DATA_TYPE_SXYZ;
				} else if (buffer instanceof SGVXYDataBuffer) {
					type = DATA_TYPE_VXY;
				} else if (buffer instanceof SGSXYZGridDataBuffer) {
					type = DATA_TYPE_SXYZ_GRID;
				} else if (buffer instanceof SGVXYGridDataBuffer) {
					type = DATA_TYPE_VXY_GRID;
				}
				dataTypes[ii] = type;
			}
			st.dataTypeArrayPointer = toPointer(dataTypes);
		
			ArrayStructure[] dsArray = new ArrayStructure[buffers.length];
			for (int ii = 0; ii < dsArray.length; ii++) {
				final int dataType = dataTypes[ii];
				if (dataType == DATA_TYPE_SXY) {
					dsArray[ii] = new SXYDataStructure();
				} else if (dataType == DATA_TYPE_SXYZ) {
					dsArray[ii] = new SXYZDataStructure();
				} else if (dataType == DATA_TYPE_VXY) {
					dsArray[ii] = new VXYDataStructure();
				} else if (dataType == DATA_TYPE_SXYZ_GRID) {
					dsArray[ii] = new SXYZGridDataStructure();
				} else if (dataType == DATA_TYPE_VXY_GRID) {
					dsArray[ii] = new VXYGridDataStructure();
				}
			}
			
			Pointer[] pointers = new Pointer[buffers.length];
			for (int ii = 0; ii < buffers.length; ii++) {
				SGDataBuffer buffer = buffers[ii];
				Pointer pBuffer = dsArray[ii].getPointer();
				if (buffer instanceof SGSXYDataBuffer) {
					SGSXYDataBuffer sxyBuffer = (SGSXYDataBuffer) buffer;
					write(sxyBuffer.getMultipleDataBuffer(), pBuffer);
				} else if (buffer instanceof SGSXYMultipleDataBuffer) {
					write((SGSXYMultipleDataBuffer) buffer, pBuffer);
				} else if (buffer instanceof SGSXYZDataBuffer) {
					write((SGSXYZDataBuffer) buffer, pBuffer);
				} else if (buffer instanceof SGVXYDataBuffer) {
					write((SGVXYDataBuffer) buffer, pBuffer);
				} else if (buffer instanceof SGSXYZGridDataBuffer) {
					write((SGSXYZGridDataBuffer) buffer, pBuffer);
				} else if (buffer instanceof SGVXYGridDataBuffer) {
					write((SGVXYGridDataBuffer) buffer, pBuffer);
				}
				pointers[ii] = pBuffer;
			}
			st.dataArrayPointer = toPointer(pointers);
			
			st.write();
		}
		
		public static Pointer toPointer(SGDataBuffer[] buffers) {
			DataArrayStructure.dataNum = buffers.length;
			DataArrayStructure headBuffer = new DataArrayStructure();
			Pointer p = headBuffer.getPointer();
			Writer.write(buffers, p);
			return p;
		}
		
		public static void write(SGDataPluginInput input, Pointer p) {
			InputDataStructure st = new InputDataStructure(p);
			st.dataBufferPointer = toPointer(input.getDataBufffers());
			String[] parameters = input.getParameters();
			st.parametersNum = parameters.length;
			st.parametersPointer = toPointer(parameters);
			st.write();
		}
		
		public static Pointer toPointer(SGDataPluginInput input) {
			InputDataStructure st = new InputDataStructure();
			Pointer p = st.getPointer();
			Writer.write(input, p);
			return p;
		}
		
		public static int toBooleanValue(final boolean b) {
			return b ? TRUE : FALSE;
		}
	}
	
	public static class Reader {

		public static int[] readIntegerArray(Pointer p, final int len) {
			int[] values = new int[len];
			if (len == 0) {
				return values;
			}
			for (int ii = 0; ii < len; ii++) {
				Pointer pValue = p.share(ii * SIZE_OF_INT);
				values[ii] = pValue.getInt(0);
			}
			return values;
		}

		public static double[] readDoubleArray(Pointer p, final int len) {
			double[] values = new double[len];
			if (len == 0) {
				return values;
			}
			for (int ii = 0; ii < len; ii++) {
				Pointer pValue = p.share(ii * SIZE_OF_DOUBLE);
				values[ii] = pValue.getDouble(0);
			}
			return values;
		}

		public static double[][] readTwoDimensionalDoubleArray(Pointer p, 
				final int lenX, final int lenY) {
			double[][] values = new double[lenY][lenX];
			if (lenX == 0 || lenY == 0) {
				return values;
			}
			Pointer[] pointers = readPointerArray(p, lenY);
			for (int ii = 0; ii < lenY; ii++) {
				values[ii] = readDoubleArray(pointers[ii], lenX);
			}
			return values;
		}

		public static Pointer[] readPointerArray(Pointer p, final int len) {
			Pointer head = p;
			Pointer[] pointers = new Pointer[len];
			if (len == 0) {
				return pointers;
			}
			for (int ii = 0; ii < len; ii++) {
				Pointer pst = head.share(ii * SIZE_OF_POINTER);
				PointerStructure st = new PointerStructure(pst);
				pointers[ii] = st.pointer;
			}
			return pointers;
		}

		public static String[] readStringArray(Pointer p, final int len) {
			Pointer head = p;
			String[] strArray = new String[len];
			if (len == 0) {
				return strArray;
			}
			for (int ii = 0; ii < len; ii++) {
				Pointer pst = head.share(ii * SIZE_OF_POINTER);
				StringStructure st = new StringStructure(pst);
				strArray[ii] = st.str;
			}
			return strArray;
		}
		
		public static SGSXYMultipleDataBuffer readSXYMultipleDataBuffer(Pointer p) {
			SXYDataStructure st = new SXYDataStructure(p);
			final int num = st.num;
			final int len = st.len;
			Pointer[] xValuePointers = readPointerArray(st.xValuesPointer, num);
			Pointer[] yValuePointers = readPointerArray(st.yValuesPointer, num);
			double[][] xValues = new double[num][];
			double[][] yValues = new double[num][];
			for (int ii = 0; ii < num; ii++) {
				xValues[ii] = readDoubleArray(xValuePointers[ii], len);
				yValues[ii] = readDoubleArray(yValuePointers[ii], len);
			}
			return new SGSXYMultipleDataBuffer(xValues, yValues);
		}

		public static SGSXYZDataBuffer readSXYZDataBuffer(Pointer p) {
			SXYZDataStructure st = new SXYZDataStructure(p);
			final int len = st.len;
			double[] xValues = readDoubleArray(st.xValuesPointer, len);
			double[] yValues = readDoubleArray(st.yValuesPointer, len);
			double[] zValues = readDoubleArray(st.zValuesPointer, len);
			return new SGSXYZDataBuffer(xValues, yValues, zValues);
		}

		public static SGVXYDataBuffer readVXYDataBuffer(Pointer p) {
			VXYDataStructure st = new VXYDataStructure(p);
			final int len = st.len;
			double[] xValues = readDoubleArray(st.xValuesPointer, len);
			double[] yValues = readDoubleArray(st.yValuesPointer, len);
			double[] fValues = readDoubleArray(st.fValuesPointer, len);
			double[] sValues = readDoubleArray(st.sValuesPointer, len);
			final Boolean polarFlag = readBooleanValue(st.polarFlag);
			if (polarFlag == null) {
				return null;
			}
			return new SGVXYDataBuffer(xValues, yValues, fValues, sValues, polarFlag);
		}

		public static SGSXYZGridDataBuffer readSXYZGridDataBuffer(Pointer p) {
			SXYZGridDataStructure st = new SXYZGridDataStructure(p);
			final int xLen = st.xLen;
			final int yLen = st.yLen;
			double[] xValues = readDoubleArray(st.xValuesPointer, xLen);
			double[] yValues = readDoubleArray(st.yValuesPointer, yLen);
			double[][] zValues = readTwoDimensionalDoubleArray(st.zValuesPointer, xLen, yLen);
			return new SGSXYZGridDataBuffer(xValues, yValues, zValues);
		}

		public static SGVXYGridDataBuffer readVXYGridDataBuffer(Pointer p) {
			VXYGridDataStructure st = new VXYGridDataStructure(p);
			final int xLen = st.xLen;
			final int yLen = st.yLen;
			Boolean polarFlag = readBooleanValue(st.polarFlag);
			if (polarFlag == null) {
				return null;
			}
			double[] xValues = readDoubleArray(st.xValuesPointer, xLen);
			double[] yValues = readDoubleArray(st.yValuesPointer, yLen);
			double[][] fValues = readTwoDimensionalDoubleArray(st.fValuesPointer, xLen, yLen);
			double[][] sValues = readTwoDimensionalDoubleArray(st.sValuesPointer, xLen, yLen);
			return new SGVXYGridDataBuffer(xValues, yValues, fValues, sValues, polarFlag);
		}

		public static SGDataBuffer[] readDataBufferArray(Pointer p) {
			DataArrayStructure st = new DataArrayStructure(p);
			final int num = st.num;
			int[] dataTypeArray = readIntegerArray(st.dataTypeArrayPointer, num);
			SGDataBuffer[] bufferArray = new SGDataBuffer[num];
			Pointer[] dataArrayPointerArray = readPointerArray(st.dataArrayPointer, num);
			Pointer pBuffer = null;
			for (int ii = 0; ii < num; ii++) {
				final int dataType = dataTypeArray[ii];
				pBuffer = dataArrayPointerArray[ii];
				SGDataBuffer buffer = null;
				switch (dataType) {
				case DATA_TYPE_SXY:
					buffer = readSXYMultipleDataBuffer(pBuffer);
					break;
				case DATA_TYPE_SXYZ:
					buffer = readSXYZDataBuffer(pBuffer);
					break;
				case DATA_TYPE_VXY:
					buffer = readVXYDataBuffer(pBuffer);
					break;
				case DATA_TYPE_SXYZ_GRID:
					buffer = readSXYZGridDataBuffer(pBuffer);
					break;
				case DATA_TYPE_VXY_GRID:
					buffer = readVXYGridDataBuffer(pBuffer);
					break;
				default:
					return null;
				}
				if (buffer == null) {
					return null;
				}
				bufferArray[ii] = buffer;
			}
			return bufferArray;
		}
		
		public static SGDataPluginOutput readOutput(Pointer p) {
			OutputDataStructure st = new OutputDataStructure(p);
			SGDataBuffer[] dataBuffers = readDataBufferArray(st.dataBufferPointer);
			if (dataBuffers == null) {
				return null;
			}
			if (st.errmsgNum < 0) {
				return null;
			}
			if (st.errmsgNum > 0 && st.errmsgPointer == null) {
				return null;
			}
			String[] errorMessages = readStringArray(st.errmsgPointer, st.errmsgNum);
			SGDataPluginOutput output = new SGDataPluginOutput(dataBuffers, errorMessages);
			return output;
		}
		
		public static Boolean readBooleanValue(final int value) {
			Boolean ret = null;
			if (value == TRUE) {
				ret = true;
			} else if (value == FALSE) {
				ret = false;
			}
			return ret;
		}
	}
	
}

