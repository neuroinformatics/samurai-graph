package jp.riken.brain.ni.samuraigraph.data;

import java.util.HashMap;

import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;

import org.w3c.dom.Element;

import ucar.nc2.Dimension;

public class SGSXYNetCDFDateData extends SGSXYNetCDFData {

    private boolean mIsTickLabelSubstitue = false;

	public SGSXYNetCDFDateData(final SGNetCDFFile ncfile, SGDataSourceObserver obs,
			final SGNetCDFDataColumnInfo xInfo,
			final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo leInfo,
			final SGNetCDFDataColumnInfo ueInfo,
			final SGNetCDFDataColumnInfo ehInfo,
			final SGNetCDFDataColumnInfo tlInfo,
			final SGNetCDFDataColumnInfo thInfo,
			final SGNetCDFDataColumnInfo timeInfo,
			final SGNetCDFDataColumnInfo serialNumberInfo,
			final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet serialNumberStride,
			final SGIntegerSeriesSet tickLabelStride,
			final boolean strideAvailable) {

        super(ncfile, obs, xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo,
        		timeInfo, serialNumberInfo, stride, serialNumberStride, tickLabelStride,
        		strideAvailable);

        if (this.mTickLabelVariable==null) {
            if (this.mXVariable instanceof SGDateVariable) {
                this.mTickLabelVariable = this.mXVariable;
                this.mTickLabelHolderVariable = this.mYVariable;
            } else if (this.mYVariable instanceof SGDateVariable) {
                this.mTickLabelVariable = this.mYVariable;
                this.mTickLabelHolderVariable = this.mXVariable;
            }
            this.mIsTickLabelSubstitue = true;
        }
    }

    @Override
    public boolean isTickLabelAvailable() {
        return (this.mTickLabelVariable != null);
    }

    @Override
    public Boolean isTickLabelHorizontal() {
        if (this.isTickLabelAvailable()) {
            return (this.mTickLabelHolderVariable.equals(this.mYVariable));
        } else {
            return null;
        }
    }

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
    @Override
    public SGNetCDFVariable[] getAssignedVariables() {
        final boolean be = this.isErrorBarAvailable();
        final boolean bt = this.isTickLabelAvailable() && this.mIsTickLabelSubstitue==false;
        if (be && bt) {
            return new SGNetCDFVariable[]{ this.mXVariable, this.mYVariable,
                    this.mLowerErrorVariable, this.mUpperErrorVariable,
                    this.mTickLabelVariable};
        } else if (be && !bt) {
            return new SGNetCDFVariable[]{ this.mXVariable, this.mYVariable,
                    this.mLowerErrorVariable, this.mUpperErrorVariable };
        } else if (!be && bt) {
            return new SGNetCDFVariable[]{ this.mXVariable, this.mYVariable,
                    this.mTickLabelVariable};
        } else {
            return new SGNetCDFVariable[]{ this.mXVariable, this.mYVariable };
        }
    }

    @Override
    public boolean writeProperty(Element el, final SGExportParameter type) {
        if (super.writeProperty(el, type) == false) {
            return false;
        }
        el.setAttribute(KEY_X_VALUE_NAMES, this.mXVariable.getName());
        el.setAttribute(KEY_Y_VALUE_NAMES, this.mYVariable.getName());
        if (this.isErrorBarAvailable()) {
            el.setAttribute(KEY_LOWER_ERROR_VALUE_NAMES, this.mLowerErrorVariable.getName());
            el.setAttribute(KEY_UPPER_ERROR_VALUE_NAMES, this.mUpperErrorVariable.getName());
            el.setAttribute(KEY_ERROR_BAR_HOLDER_NAMES, this.mErrorBarHolderVariable.getName());
        }
        if (this.isTickLabelAvailable() && this.mIsTickLabelSubstitue==false) {
            el.setAttribute(KEY_TICK_LABEL_NAMES, this.mTickLabelVariable.getName());
            el.setAttribute(KEY_TICK_LABEL_HOLDER_NAMES, this.mTickLabelHolderVariable.getName());
        }
        return true;
    }

	@Override
	public SGISXYTypeMultipleData toMultiple() {
		// Convert this class (SGSXYNetCDFData) to class
		// (SGSXYMultipleVariableNetCDFData).
		final boolean be = this.isErrorBarAvailable();
		final boolean bt = this.isTickLabelAvailable()
				&& !this.mIsTickLabelSubstitue;
		SGNetCDFDataColumnInfo[] x = SGDataUtility.createDataColumnInfoArray(this.mXVariable, X_VALUE);
		SGNetCDFDataColumnInfo[] y = SGDataUtility.createDataColumnInfoArray(this.mYVariable, Y_VALUE);
		SGNetCDFDataColumnInfo[] le = be ? new SGNetCDFDataColumnInfo[] { this.createErrorBarInfo(
				this.mLowerErrorVariable, LOWER_ERROR_VALUE) } : null;
		SGNetCDFDataColumnInfo[] ue = be ? new SGNetCDFDataColumnInfo[] { this.createErrorBarInfo(
				this.mUpperErrorVariable, UPPER_ERROR_VALUE) } : null;
		SGNetCDFDataColumnInfo[] eh = be ? this.getErrorBarHolderInfo(x, y) : null;
		SGNetCDFDataColumnInfo[] tl = bt ? new SGNetCDFDataColumnInfo[] { SGDataUtility
				.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL,
						this.mTickLabelHolderVariable.getName()) } : null;
		SGNetCDFDataColumnInfo[] th = bt ? this.getTickLabelHolderInfo(x, y) : null;
		SGNetCDFDataColumnInfo time = (this.mTimeVariable != null) ? SGDataUtility
				.createDataColumnInfo(this.mTimeVariable, ANIMATION_FRAME) : null;
		SGNetCDFDataColumnInfo serialNumber = this.isIndexAvailable() ? SGDataUtility
				.createDataColumnInfo(this.mIndexVariable, INDEX) : null;
		SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
				this.getNetcdfFile(), this.getDataSourceObserver(), x, y, le, ue, eh, tl, th, time,
				serialNumber, this.mStride, this.mTickLabelStride, this.isStrideAvailable());
		data.mOriginMap = new HashMap<String, Integer>(this.mOriginMap);
		data.setDecimalPlaces(this.mDecimalPlaces);
		data.setExponent(this.mExponent);
        SGDataCache cache = this.getCache();
        if (cache != null) {
			data.setCache(new SGSXYMultipleDataCache((SGSXYDataCache) cache));
		}
		data.setTimeStride(this.mTimeStride);
		return data;
	}

	@Override
	// Override for the condition
    protected SGNetCDFDataColumnInfo createTickLabelInfo(SGNetCDFVariable var) {
		if (this.isTickLabelAvailable() && !this.mIsTickLabelSubstitue) {
			String thName = this.mTickLabelHolderVariable.getName();
			return SGDataUtility.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL, thName);
		} else {
			return null;
		}
    }

	@Override
	public SGISXYTypeMultipleData toMultiple(final Dimension dim,
			final int pickUpIndex, final int len) {
		SGNetCDFDataColumnInfo x = SGDataUtility.createDataColumnInfo(this.mXVariable, X_VALUE);
		SGNetCDFDataColumnInfo y = SGDataUtility.createDataColumnInfo(this.mYVariable, Y_VALUE);
		SGNetCDFDataColumnInfo le = null;
		SGNetCDFDataColumnInfo ue = null;
		SGNetCDFDataColumnInfo eh = null;
		if (this.isErrorBarAvailable()) {
			le = this.createErrorBarInfo(this.mLowerErrorVariable, LOWER_ERROR_VALUE);
			ue = this.createErrorBarInfo(this.mUpperErrorVariable, UPPER_ERROR_VALUE);
			eh = this.getErrorBarHolderInfo(x, y);
		}
		SGNetCDFDataColumnInfo tl = null;
		SGNetCDFDataColumnInfo th = null;
		if (this.isTickLabelAvailable()) {
			String thName = this.mTickLabelHolderVariable.getName();
			tl = SGDataUtility.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL, thName);
			th = this.getTickLabelHolderInfo(x, y);
		}
		SGNetCDFDataColumnInfo time = (this.mTimeVariable != null) ? SGDataUtility
				.createDataColumnInfo(this.mTimeVariable, ANIMATION_FRAME) : null;
		SGNetCDFDataColumnInfo id = this.isIndexAvailable() ? SGDataUtility
				.createDataColumnInfo(this.mIndexVariable, INDEX) : null;
		SGIntegerSeriesSet pickUpIndices;
		if (pickUpIndex == len - 1) {
			pickUpIndices = SGIntegerSeriesSet.parse(SGIntegerSeries.ARRAY_INDEX_END, len);
		} else {
			pickUpIndices = new SGIntegerSeriesSet(pickUpIndex);
		}
		SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
				this.getNetcdfFile(), this.getDataSourceObserver(), x, y, le, ue, eh, tl, th, dim.getName(),
				pickUpIndices, time, id, this.mStride, this.mTickLabelStride, this.isStrideAvailable());
        SGDataCache cache = this.getCache();
        if (cache != null) {
			data.setCache(new SGSXYMultipleDataCache((SGSXYDataCache) cache));
		}
		data.setTimeStride(this.mTimeStride);
		return data;
	}

}
