package jp.riken.brain.ni.samuraigraph.data;

/**
 * The data column for the sampling rate.
 *
 */
public class SGSamplingDataColumn extends SGNumberDataColumn {

    /**
     * The sampling rate.
     */
    private double mSamplingRate;

    /**
     * The array length;
     */
    private int mLength;

    /**
     * Builds a column with given sampling rate.
     * 
     * @param samplingRate
     *            the sampling rate
     */
	public SGSamplingDataColumn(final double samplingRate, final int length) {
		super();

        if (samplingRate <= 0.0) {
            throw new IllegalArgumentException("samplingRate <= 0.0: " + samplingRate);
        }
        if (length < 0) {
            throw new IllegalArgumentException("length < 0 :" + length);
        }

		// set to the attribute
		this.mSamplingRate = samplingRate;
		this.mLength = length;
		
		// initialize the title
		this.mTitle = SGDataUtility.createSamplingRateTitle(samplingRate);
	}

    /**
     * Returns the length of data column.
     * 
     * @return the length of data column
     */
	public int getLength() {
		return this.mLength;
	}

    /**
     * Returns the value type of this column.
     * 
     * @return
     *         the value type of this column
     */
    public String getValueType() {
        return SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE;
    }
    
    /**
     * Returns a value in this data column at given row index.
     * 
     * @param rowIndex
     *            the row index
     * @return a value at given row index
     */
	public Object getValue(final int rowIndex) {
        return Double.valueOf(this.calcValue(rowIndex));
	}
	
	/**
	 * Calculates the sampling value at gien index.
	 * 
	 * @param n
	 *          the index
	 * @return the sampling value
	 */
	private double calcValue(final int n) {
		return (n / this.mSamplingRate);
	}

    /**
     * Returns the sampling rate.
     * 
     * @return
     *         the sampling rate
     */
    public double getSamplingRate() {
        return this.mSamplingRate;
    }

    /**
     * Returns an array of numbers.
     * 
     * @return an array of numbers
     */
	public double[] getNumberArray() {
		double[] array = new double[this.mLength];
		for (int ii = 0; ii < this.mLength; ii++) {
			array[ii] = this.calcValue(ii);
		}
		return array;
	}

}
