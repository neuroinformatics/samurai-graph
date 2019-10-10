package jp.riken.brain.ni.samuraigraph.data;

public abstract class SGDataValue {

	protected SGDataValue() {
		super();
	}
	
	public static class Value {
		
		public double number;
		
		public boolean missing;

		public Value(final double number, final boolean missing) {
			super();
			this.number = number;
			this.missing = missing;
		}
		
		public Value(final double number) {
			this(number, false);
		}
		
		@Override
		public String toString() {
			return Double.toString(this.number);
		}
	}
	
	public static class SXYSingleDataValue extends SGDataValue {
		
		public double xValue;
		
		public double yValue;
		
		public SXYSingleDataValue() {
			super();
		}
	}
	
	public static class SXYDoubleDataValue extends SGDataValue {
		
		public double xValue0;
		
		public double yValue0;
		
		public double xValue1;
		
		public double yValue1;
		
		public SXYDoubleDataValue() {
			super();
		}
	}

	public static class SXYZDataValue extends SGDataValue {
		
		public double xValue;
		
		public double yValue;
		
		public Value zValue;
		
		public SXYZDataValue() {
			super();
		}
	}

	public static class VXYDataValue extends SGDataValue {
		
		public double xValue;
		
		public double yValue;
		
		public Value fValue;

		public Value sValue;

		public VXYDataValue() {
			super();
		}
	}

}
