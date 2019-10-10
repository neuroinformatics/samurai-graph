package jp.riken.brain.ni.samuraigraph.data;

/**
 * The class for dimension of multidimensional data.
 *
 */
public class SGMDArrayDimensionInfo {

	/**
	 * The name.
	 */
	private String mName = null;

	/**
	 * The index of dimension.
	 */
	private int mIndex = -1;

	/**
	 * Builds an object.
	 * 
	 * @param name
	 *          the name
	 * @param index
	 *          the index of dimension
	 */
	public SGMDArrayDimensionInfo(final String name, final int index) {
		super();
		this.mName = name;
		this.mIndex = index;
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * Returns the index of dimension.
	 * 
	 * @return the index of dimension
	 */
	public int getIndex() {
		return this.mIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGMDArrayDimensionInfo)) {
			return false;
		}
		SGMDArrayDimensionInfo info = (SGMDArrayDimensionInfo) obj;
		if (!this.mName.equals(info.mName)) {
			return false;
		}
		if (this.mIndex != info.mIndex) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[name=");
		sb.append(this.mName);
		sb.append(", index=");
		sb.append(this.mIndex);
		sb.append(']');
		return sb.toString();
	}
}
