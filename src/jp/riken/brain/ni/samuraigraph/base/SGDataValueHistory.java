package jp.riken.brain.ni.samuraigraph.base;






/**
 * The base class of data value.
 *
 */
public abstract class SGDataValueHistory implements Cloneable {
	
	protected double mValue;
	
	protected String mColumnType;
	
	protected SGDataValueHistory mPreviousValue;
	
	private int mColIndex;
	
	private int mRowIndex;
	
	protected SGDataValueHistory(final double value, final String columnType,
			final int col, final int row) {
		super();
		this.mValue = value;
		this.mColumnType = columnType;
		this.mColIndex = col;
		this.mRowIndex = row;
	}
	
	protected SGDataValueHistory(final double value, final String columnType,
			final int index) {
		this(value, columnType, 0, index);
	}
	
	public int getColumnIndex() {
		return this.mColIndex;
	}
	
	public int getRowIndex() {
		return this.mRowIndex;
	}


	public double getValue() {
		return this.mValue;
	}
	
	public String getColumnType() {
		return this.mColumnType;
	}
	
	public SGDataValueHistory getPreviousValue() {
		return this.mPreviousValue;
	}
	
	@Override
    public Object clone() {
        try {
        	return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGDataValueHistory)) {
			return false;
		}
		SGDataValueHistory value = (SGDataValueHistory) obj;
		if (this.mValue != value.mValue) {
			return false;
		}
		if (!SGUtility.equals(this.mColumnType, value.mColumnType)) {
			return false;
		}
		if (this.mColIndex != value.mColIndex) {
			return false;
		}
		if (this.mRowIndex != value.mRowIndex) {
			return false;
		}
		return true;
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(this.paramString());
		sb.append("]");
		return sb.toString();
	}
	
	protected String paramString() {
		StringBuffer sb = new StringBuffer();
		sb.append("value=");
		sb.append(this.mValue);
		sb.append(", prevValue=");
		if (this.mPreviousValue != null) {
			sb.append(this.mPreviousValue.getValue());
		}
		sb.append(", columnType=");
		sb.append(this.mColumnType);
		sb.append(", col=");
		sb.append(this.mColIndex);
		sb.append(", row=");
		sb.append(this.mRowIndex);
		return sb.toString();
	}
	
	public static abstract class SDArray extends SGDataValueHistory {
		
		public SDArray(final double value, final String columnType,
				final int index) {
			super(value, columnType, index);
		}

		protected SDArray(final double value, final String columnType,
				final int col, final int row) {
			super(value, columnType, col, row);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SDArray)) {
				return false;
			}
			if (!super.equals(obj)) {
				return false;
			}
			return true;
		}
		
		public static class D1 extends SDArray implements ISingleDimension {
			
			public D1(final double value, final String columnType,
					final int index) {
				super(value, columnType, index);
			}

			public D1(final double value, final String columnType,
					final int index, final double prevValue) {
				this(value, columnType, index);
				this.mPreviousValue = new D1(prevValue, columnType, index);
			}

			public int getIndex() {
				return this.getRowIndex();
			}
		}
		
		public static class MD1 extends SDArray implements IMultiple {
			
			public MD1(final double value, String columnType, final int childIndex,
					final int index) {
				super(value, columnType, childIndex, index);
			}

			public MD1(final double value, String columnType, final int childIndex,
					final int index, final double prevValue) {
				this(value, columnType, childIndex, index);
				this.mPreviousValue = new MD1(prevValue, columnType, childIndex, index);
			}

			public int getChildIndex() {
				return this.getColumnIndex();
			}
			
			public int getIndex() {
				return this.getRowIndex();
			}
		}
	}
	
	public static abstract class NetCDF extends SGDataValueHistory {
		
		private String mVarName = null;
		
		private String mAnimationDimName = null;
		
		private int mAnimationDimIndex = -1;

		public NetCDF(final double value, String columnType, final int col,
				final int row, String varName) {
			super(value, columnType, col, row);
			this.mVarName = varName;
		}

		public void setAnimationInfo(String dimName, final int dimIndex) {
			this.mAnimationDimName = dimName;
			this.mAnimationDimIndex = dimIndex;
			if (this.mPreviousValue != null) {
				((NetCDF) this.mPreviousValue).setAnimationInfo(dimName, dimIndex);
			}
		}
		
		public String getVarName() {
			return this.mVarName;
		}
		
		public String getAnimationDimName() {
			return this.mAnimationDimName;
		}
		
		public int getAnimationDimIndex() {
			return this.mAnimationDimIndex;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NetCDF)) {
				return false;
			}
			NetCDF value = (NetCDF) obj;
			if (!SGUtility.equals(this.mVarName, value.mVarName)) {
				return false;
			}
			if (!SGUtility.equals(this.mAnimationDimName, value.mAnimationDimName)) {
				return false;
			}
			if (this.mAnimationDimIndex != value.mAnimationDimIndex) {
				return false;
			}
			return true;
		}
		
		@Override
		protected String paramString() {
			StringBuffer sb = new StringBuffer();
			sb.append(super.paramString());
			sb.append(", var=");
			sb.append(this.mVarName);
			sb.append(", animation=");
			sb.append(this.mAnimationDimName);
			sb.append("(");
			sb.append(this.mAnimationDimIndex);
			sb.append(")");
			return sb.toString();
		}
		
		public static class D1 extends NetCDF implements ISingleDimension {
			
			private String mIndexDimName = null;
			
			public D1(final double value, String columnType, final int col,
					final int row, String varName) {
				super(value, columnType, col, row, varName);
			}

			public D1(final double value, String columnType, final int col,
					final int row, String varName, final double prevValue) {
				this(value, columnType, col, row, varName);
				this.mPreviousValue = new D1(prevValue, columnType, col, row, varName);
			}

			public D1(final double value, final String columnType,
					final int index, String varName) {
				super(value, columnType, 0, index, varName);
			}

			public D1(final double value, final String columnType,
					final int index, String varName, final double prevValue) {
				this(value, columnType, index, varName);
				this.mPreviousValue = new D1(prevValue, columnType, index, varName);
			}

			public String getIndexDimName() {
				return this.mIndexDimName;
			}

			public void setIndexDimName(String name) {
				this.mIndexDimName = name;
				if (this.mPreviousValue != null) {
					((D1) this.mPreviousValue).setIndexDimName(name);
				}
			}
			
			public int getIndex() {
				return this.getRowIndex();
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof D1)) {
					return false;
				}
				if (!super.equals(obj)) {
					return false;
				}
				D1 value = (D1) obj;
				if (!SGUtility.equals(this.mIndexDimName, value.mIndexDimName)) {
					return false;
				}
				return true;
			}

			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append(this.mIndexDimName);
				return sb.toString();
			}
		}
		
		public static class D2 extends NetCDF {
			
			private String mXDimName = null;
			
			private String mYDimName = null;
			
			public D2(final double value, String columnType, final int col,
					final int row, String varName) {
				super(value, columnType, col, row, varName);
			}

			public D2(final double value, String columnType, final int col,
					final int row, String varName, final double prevValue) {
				this(value, columnType, col, row, varName);
				this.mPreviousValue = new D2(prevValue, columnType, col, row, varName);
			}

			public void setXYDimName(String xDimName, String yDimName) {
				this.mXDimName = xDimName;
				this.mYDimName = yDimName;
				if (this.mPreviousValue != null) {
					((D2) this.mPreviousValue).setXYDimName(xDimName, yDimName);
				}
			}
			
			public String getXDimName() {
				return this.mXDimName;
			}
			
			public String getYDimName() {
				return this.mYDimName;
			}
			
			public int getXIndex() {
				return this.getColumnIndex();
			}
			
			public int getYIndex() {
				return this.getRowIndex();
			}
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof D2)) {
					return false;
				}
				if (!super.equals(obj)) {
					return false;
				}
				D2 value = (D2) obj;
				if (!SGUtility.equals(this.mXDimName, value.mXDimName)) {
					return false;
				}
				if (!SGUtility.equals(this.mYDimName, value.mYDimName)) {
					return false;
				}
				return true;
			}
			
			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append("(");
				sb.append(this.mXDimName);
				sb.append(",");
				sb.append(this.mYDimName);
				sb.append(")");
				return sb.toString();
			}
		}
		
		public static class MD1 extends NetCDF implements IMultiple {
			
			private String mDimName = null;
			
			private String mPickUpDimName = null;
			
			private int mPickUpDimIndex = -1;
			
			public MD1(final double value, String columnType, final int childIndex,
					final int index, String varName) {
				super(value, columnType, childIndex, index, varName);
			}

			public MD1(final double value, String columnType, final int childIndex,
					final int index, String varName, final double prevValue) {
				this(value, columnType, childIndex, index, varName);
				this.mPreviousValue = new MD1(prevValue, columnType, childIndex, index, varName);
			}

			public int getChildIndex() {
				return this.getColumnIndex();
			}
			
			public int getIndex() {
				return this.getRowIndex();
			}

			public void setDimInfo(String dimName) {
				this.mDimName = dimName;
			}
			
			public void setPickUpInfo(String dimName, final int dimIndex) {
				this.mPickUpDimName = dimName;
				this.mPickUpDimIndex = dimIndex;
			}
			
			public String getDimName() {
				return this.mDimName;
			}
			
			public String getPickUpDimName() {
				return this.mPickUpDimName;
			}
			
			public int getPickUpDimIndex() {
				return this.mPickUpDimIndex;
			}
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof MD1)) {
					return false;
				}
				MD1 value = (MD1) obj;
				if (!SGUtility.equals(this.mDimName, value.mDimName)) {
					return false;
				}
				if (!SGUtility.equals(this.mPickUpDimName, value.mPickUpDimName)) {
					return false;
				}
				if (this.mPickUpDimIndex != value.mPickUpDimIndex) {
					return false;
				}
				return true;
			}
			
			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append(this.mDimName);
				sb.append(", pickup=");
				sb.append(this.mPickUpDimName);
				sb.append("(");
				sb.append(this.mPickUpDimIndex);
				sb.append(")");
				return sb.toString();
			}
		}

	}

	public static abstract class MDArray extends SGDataValueHistory {
		
		private String mVarName = null;
		
		private int mAnimationDimension = -1;
		
		private int mAnimationDimIndex = -1;

		public MDArray(final double value, String columnType, final int col,
				final int row, String varName) {
			super(value, columnType, col, row);
			this.mVarName = varName;
		}
		
		public MDArray(final double value, final String columnType,
				final int index, String varName) {
			this(value, columnType, 0, index, varName);
		}

		public void setAnimationInfo(final int dim, final int dimIndex) {
			this.mAnimationDimension = dim;
			this.mAnimationDimIndex = dimIndex;
			if (this.mPreviousValue != null) {
				((MDArray) this.mPreviousValue).setAnimationInfo(dim, dimIndex);
			}
		}
		
		public String getVarName() {
			return this.mVarName;
		}
		
		public int getAnimationDimension() {
			return this.mAnimationDimension;
		}
		
		public int getAnimationDimIndex() {
			return this.mAnimationDimIndex;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MDArray)) {
				return false;
			}
			MDArray value = (MDArray) obj;
			if (!SGUtility.equals(this.mVarName, value.mVarName)) {
				return false;
			}
			if (this.mAnimationDimension != value.mAnimationDimension) {
				return false;
			}
			if (this.mAnimationDimIndex != value.mAnimationDimIndex) {
				return false;
			}
			return true;
		}
		
		@Override
		protected String paramString() {
			StringBuffer sb = new StringBuffer();
			sb.append(super.paramString());
			sb.append(", var=");
			sb.append(this.mVarName);
			sb.append(", animation=");
			sb.append(this.mAnimationDimension);
			sb.append("(");
			sb.append(this.mAnimationDimIndex);
			sb.append(")");
			return sb.toString();
		}
		
		public static class D1 extends MDArray implements ISingleDimension {
			
			private int mDimension= -1;
			
			public D1(final double value, String columnType, final int col,
					final int row, String varName) {
				super(value, columnType, col, row, varName);
			}

			public D1(final double value, String columnType, final int col,
					final int row, String varName, final double prevValue) {
				this(value, columnType, col, row, varName);
				this.mPreviousValue = new D1(prevValue, columnType, col, row, varName);
			}

			public D1(final double value, final String columnType,
					final int index, String varName) {
				this(value, columnType, 0, index, varName);
			}

			public D1(final double value, final String columnType,
					final int index, String varName, final double prevValue) {
				this(value, columnType, index, varName);
				this.mPreviousValue = new D1(prevValue, columnType, index, varName);
			}

			public int getIndex() {
				return this.getRowIndex();
			}

			public void setDimension(final int dim) {
				this.mDimension = dim;
				if (this.mPreviousValue != null) {
					((D1) this.mPreviousValue).setDimension(dim);
				}
			}
			
			public int getDimension() {
				return this.mDimension;
			}
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof D1)) {
					return false;
				}
				if (!super.equals(obj)) {
					return false;
				}
				D1 value = (D1) obj;
				if (this.mDimension != value.mDimension) {
					return false;
				}
				return true;
			}

			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append(this.mDimension);
				return sb.toString();
			}
		}
		
		public static class D2 extends MDArray {
			
			private int mXDimension= -1;
			
			private int mYDimension= -1;
			
			public D2(final double value, String columnType, final int col,
					final int row, String varName) {
				super(value, columnType, col, row, varName);
			}

			public D2(final double value, String columnType, final int col,
					final int row, String varName, final double prevValue) {
				this(value, columnType, col, row, varName);
				this.mPreviousValue = new D2(prevValue, columnType, col, row, varName);
			}

			public void setXYDimension(final int xDim, final int yDim) {
				this.mXDimension = xDim;
				this.mYDimension = yDim;
				if (this.mPreviousValue != null) {
					((D2) this.mPreviousValue).setXYDimension(xDim, yDim);
				}
			}
			
			public int getXDimension() {
				return this.mXDimension;
			}
			
			public int getYDimension() {
				return this.mYDimension;
			}

			public int getXIndex() {
				return this.getColumnIndex();
			}
			
			public int getYIndex() {
				return this.getRowIndex();
			}
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof D2)) {
					return false;
				}
				if (!super.equals(obj)) {
					return false;
				}
				D2 value = (D2) obj;
				if (this.mXDimension != value.mXDimension) {
					return false;
				}
				if (this.mYDimension != value.mYDimension) {
					return false;
				}
				return true;
			}
			
			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append("(");
				sb.append(this.mXDimension);
				sb.append(",");
				sb.append(this.mYDimension);
				sb.append(")");
				return sb.toString();
			}
		}
		
		public static class MD1 extends MDArray implements IMultiple {
			
			private int mDimension= -1;
			
			private int mPickUpDimension = -1;
			
			private int mPickUpDimIndex = -1;
			
			public MD1(final double value, String columnType, final int childIndex,
					final int index, String varName) {
				super(value, columnType, childIndex, index, varName);
			}
			
			public MD1(final double value, String columnType, final int childIndex,
					final int index, String varName, final double prevValue) {
				this(value, columnType, childIndex, index, varName);
				this.mPreviousValue = new MD1(prevValue, columnType, childIndex, index, varName);
			}
			
			public int getChildIndex() {
				return this.getColumnIndex();
			}
			
			public int getIndex() {
				return this.getRowIndex();
			}

			public void setDimension(final int dim) {
				this.mDimension = dim;
				if (this.mPreviousValue != null) {
					((MD1) this.mPreviousValue).setDimension(dim);
				}
			}
			
			public void setPickUpInfo(final int dim, final int dimIndex) {
				this.mPickUpDimension = dim;
				this.mPickUpDimIndex = dimIndex;
				if (this.mPreviousValue != null) {
					((MD1) this.mPreviousValue).setPickUpInfo(dim, dimIndex);
				}
			}
			
			public int getDimension() {
				return this.mDimension;
			}
			
			public int getPickUpDimension() {
				return this.mPickUpDimension;
			}
			
			public int getPickUpDimIndex() {
				return this.mPickUpDimIndex;
			}
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof MD1)) {
					return false;
				}
				MD1 value = (MD1) obj;
				if (this.mDimension != value.mDimension) {
					return false;
				}
				if (this.mPickUpDimension != value.mPickUpDimension) {
					return false;
				}
				if (this.mPickUpDimIndex != value.mPickUpDimIndex) {
					return false;
				}
				return true;
			}
			
			@Override
			protected String paramString() {
				StringBuffer sb = new StringBuffer();
				sb.append(super.paramString());
				sb.append(", dim=");
				sb.append(this.mDimension);
				sb.append(", pickup=");
				sb.append(this.mPickUpDimension);
				sb.append("(");
				sb.append(this.mPickUpDimIndex);
				sb.append(")");
				return sb.toString();
			}
		}

	}

	public static interface IMultiple {

		public int getChildIndex();
		
		public int getIndex();
	}

	public static interface ISingleDimension {
		
		public int getIndex();
	}
}
